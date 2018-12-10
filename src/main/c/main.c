/* CC0 - free software.
To the extent possible under law, all copyright and related or neighboring
rights to this work are waived. See the LICENSE file for more information. */
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>
#ifdef __linux__
#include <signal.h>
#endif


#define MAX_RUN_LENGTH 12
#define MAX_CMD_LENGTH 256

typedef struct cmd {
    char* str;
    void (*action)();  // actions of type: void func()
} cmd_t;
void pause_action();
void resume_action();
/* Define action keywords here and set their callback functions. */
cmd_t commands[] = {
    { "pause",  pause_action  },
    { "resume", resume_action }
};
int n_commands = sizeof(commands) / sizeof(cmd_t);

int paused = 0;
int finished = 0;


/* Test worker. Just print a counter every second. Print "..." if paused. */
void run() {
    int count = 0;
    while(count < MAX_RUN_LENGTH) {
        if(!paused) {
            printf("C: %3d\n", ++count);
        } else {
            printf("C: ...\n");
        }
        fflush(stdout);
        sleep(1);
        if(finished) {
            break;
        }
    }
}

/* Set all command matcher indices to 0. */
void reset_positions(int* positions) {
    for(int i=0; i<n_commands; i++) {
        positions[i] = 0;
    }
}

/* Scan stdin for matching character sequences. All commands have an index for
 * how many of the last-read characters have matched. Consider the input:
 *               o i y p a u r e L c p a u s e X
 * The command_positions will have had the following values then:
 *   "pause":    0 0 0 1 2 3 0 0 0 0 1 2 3 4 * 0
 *   "resume":   0 0 0 0 0 0 1 2 0 0 0 0 0 0 0 0
 * At the "*" the appropriate action function will be called and the position
 * reset to zero. */
void* match_commands() {
    int command_positions[n_commands];
    reset_positions(command_positions);
    char c = 0;
    do {
        c = fgetc(stdin);
        for(int i=0; i<n_commands; i++) {
            char* cur_cmd = commands[i].str;
            int cur_cmd_pos = command_positions[i];
            if((cur_cmd[cur_cmd_pos] != '\0') &&
                    (c == cur_cmd[cur_cmd_pos])) {
                ++command_positions[i];
            } else {
                command_positions[i] = 0;
            }
            if(command_positions[i] >= strnlen(cur_cmd, MAX_CMD_LENGTH)) {
                ((commands[i]).action)();
                reset_positions(command_positions);
            }
        }
    } while(c != EOF);
    printf("C: Parent finished\n");
    paused = 0;
    finished = 1;
}


/* Pause action callback. */
void pause_action() {
    printf("C: Paused\n");
    paused = 1;
}

/* Resume action callback. */
void resume_action() {
    printf("C: Resumed\n");
    paused = 0;
}

/* Set up the stdin matcher thread. Also set up signal handling on Linux (which
 * still needs EOF on stdin before program exits). On Windows only stdin/-out
 * commands are available, on Linux one may also send SIGTSTP to pause and
 * SIGCONT to resume. */
void main(int argc, char* argv) {
#ifdef __linux__
    signal(SIGTSTP, pause_action);
    signal(SIGCONT, resume_action);
#endif
    pthread_t thread_id;
    pthread_create(&thread_id, NULL, match_commands, NULL);
    run();
    pthread_join(thread_id, NULL);
}
