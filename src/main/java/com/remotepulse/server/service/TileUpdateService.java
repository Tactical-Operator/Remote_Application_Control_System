package com.remotepulse.server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class TileUpdateService {

    /*
     * Remote Pulse currently has one Android
     * remote connected to the server.
     *
     * Therefore we only need one active
     * SSE connection.
     */
    private SseEmitter emitter;


    /*
     * Android connects to:
     *
     * GET /api/tiles/events
     *
     * If Android connects again, replace
     * the previous SSE connection.
     */
    public synchronized SseEmitter subscribe() {

        /*
         * Close the previous connection
         * if one still exists.
         */
        if (emitter != null) {

            try {

                emitter.complete();

            } catch (Exception ignored) {
            }
        }


        /*
         * Create the new SSE connection.
         *
         * 0L = no timeout.
         */
        SseEmitter newEmitter =
                new SseEmitter(0L);


        emitter = newEmitter;


        System.out.println(
                "ANDROID SSE CONNECTED"
        );

        System.out.println(
                "CONNECTED SSE CLIENTS: 1"
        );


        /*
         * If this connection finishes,
         * remove it only if it is still
         * the current connection.
         */
        newEmitter.onCompletion(() -> {

            clearEmitter(
                    newEmitter
            );

            System.out.println(
                    "ANDROID SSE COMPLETED"
            );
        });


        newEmitter.onTimeout(() -> {

            clearEmitter(
                    newEmitter
            );

            System.out.println(
                    "ANDROID SSE TIMEOUT"
            );
        });


        newEmitter.onError(error -> {

            clearEmitter(
                    newEmitter
            );

            System.out.println(
                    "ANDROID SSE ERROR: "
                            + error.getMessage()
            );
        });


        /*
         * Send an initial event immediately
         * so the SSE response is established
         * and flushed to Android.
         */
        try {

            newEmitter.send(
                    SseEmitter.event()
                            .name("connected")
                            .data("CONNECTED")
            );

        } catch (IOException e) {

            clearEmitter(
                    newEmitter
            );

            newEmitter.completeWithError(
                    e
            );
        }


        return newEmitter;
    }


    /*
     * Remove an emitter only when it is
     * still the current connection.
     *
     * This is important because an old
     * emitter may complete AFTER a new
     * Android connection has already
     * replaced it.
     */
    private synchronized void clearEmitter(
            SseEmitter emitterToRemove
    ) {

        if (emitter == emitterToRemove) {

            emitter = null;
        }
    }


    /*
     * Called whenever any tile
     * configuration changes.
     */
    public synchronized void tilesChanged() {

        System.out.println(
                "REMOTE PULSE: TILES_CHANGED EVENT TRIGGERED"
        );


        if (emitter == null) {

            System.out.println(
                    "CONNECTED SSE CLIENTS: 0"
            );

            return;
        }


        System.out.println(
                "CONNECTED SSE CLIENTS: 1"
        );


        try {

            emitter.send(
                    SseEmitter.event()
                            .name("tiles-changed")
                            .data("TILES_CHANGED")
            );

        } catch (IOException e) {

            SseEmitter failedEmitter =
                    emitter;

            emitter = null;


            try {

                failedEmitter.completeWithError(
                        e
                );

            } catch (Exception ignored) {
            }
        }
    }
}