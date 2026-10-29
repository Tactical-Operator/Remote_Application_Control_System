
package com.remotepulse.server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * Manages the Server-Sent Events (SSE) connection between
 * the Remote Pulse server and its Android client.
 *
 * Maintains the active connection and sends notifications
 * when tile configurations change.
 */
@Service
public class TileUpdateService {

        /**
         * Holds the currently active SSE connection.
         *
         * This implementation maintains one active emitter,
         * matching the current single-Android-client setup.
         */
        private SseEmitter emitter;

        /**
         * Establishes an SSE subscription for the Android client.
         *
         * If an earlier connection exists, it is completed before
         * the new connection is registered.
         *
         * @return the newly created SSE emitter
         */
        public synchronized SseEmitter subscribe() {

                // Complete the previous connection if one exists.
                if (emitter != null) {
                        try {
                                emitter.complete();
                        } catch (Exception ignored) {
                        }
                }

                // A timeout of 0L means no timeout is configured.
                SseEmitter newEmitter = new SseEmitter(0L);

                emitter = newEmitter;

                System.out.println("ANDROID SSE CONNECTED");
                System.out.println("CONNECTED SSE CLIENTS: 1");

                /*
                 * Clear the connection when it completes, but only if
                 * it is still the currently registered emitter.
                 */
                newEmitter.onCompletion(() -> {

                        clearEmitter(newEmitter);

                        System.out.println("ANDROID SSE COMPLETED");
                });

                // Handle connection timeout and release the active reference.
                newEmitter.onTimeout(() -> {

                        clearEmitter(newEmitter);

                        System.out.println("ANDROID SSE TIMEOUT");
                });

                // Handle connection errors and release the active reference.
                newEmitter.onError(error -> {

                        clearEmitter(newEmitter);

                        System.out.println(
                                        "ANDROID SSE ERROR: " + error.getMessage());
                });

                /*
                 * Send an initial event so the connection is established
                 * and the response can be flushed to the Android client.
                 */
                try {

                        newEmitter.send(
                                        SseEmitter.event()
                                                        .name("connected")
                                                        .data("CONNECTED"));

                } catch (IOException e) {

                        clearEmitter(newEmitter);

                        newEmitter.completeWithError(e);
                }

                return newEmitter;
        }

        /**
         * Removes an emitter only if it is still the active connection.
         *
         * This prevents an older connection's completion callback
         * from clearing a newer connection that has replaced it.
         *
         * @param emitterToRemove the emitter to remove
         */
        private synchronized void clearEmitter(
                        SseEmitter emitterToRemove) {

                if (emitter == emitterToRemove) {
                        emitter = null;
                }
        }

        /**
         * Notifies the connected Android client that tile
         * configurations have changed.
         *
         * If no client is connected, the method logs that state
         * and returns without sending an event.
         */
        public synchronized void tilesChanged() {

                System.out.println(
                                "REMOTE PULSE: TILES_CHANGED EVENT TRIGGERED");

                // No active connection means there is no client to notify.
                if (emitter == null) {

                        System.out.println("CONNECTED SSE CLIENTS: 0");

                        return;
                }

                System.out.println("CONNECTED SSE CLIENTS: 1");

                try {

                        // Send the tile-change notification to Android.
                        emitter.send(
                                        SseEmitter.event()
                                                        .name("tiles-changed")
                                                        .data("TILES_CHANGED"));

                } catch (IOException e) {

                        // Remove the failed connection from the active reference.
                        SseEmitter failedEmitter = emitter;
                        emitter = null;

                        try {
                                failedEmitter.completeWithError(e);
                        } catch (Exception ignored) {
                        }
                }
        }
}
