package com.project.lol.webview.injections

object AudioRecovery {
    const val CONTENT = """
        (function(){
            if(window.__splAudioRecoveryInstalled) return;
            window.__splAudioRecoveryInstalled = true;

            var lastTrack = '';
            var lastPosition = null;
            var stalledSince = 0;
            var recovering = false;
            var lastRecovery = 0;

            var STALL_MS = 6000;
            var COOLDOWN_MS = 15000;

            function now(){
                return Date.now();
            }

            function recover(){
                if(recovering) return;

                var t = now();

                if(t - lastRecovery < COOLDOWN_MS) return;

                /*
                 * Never recover when the user intentionally paused.
                 */
                if(!window.playing) {
                    stalledSince = 0;
                    return;
                }

                recovering = true;
                lastRecovery = t;

                try{
                    if(typeof AndBridge !== 'undefined' &&
                       typeof AndBridge.dbg === 'function'){
                        AndBridge.dbg('s','Audio recovery: playback stalled');
                    }
                }catch(e){}

                /*
                 * Use the existing Spotify Play/Pause controls.
                 * This does not replace or modify normal controls.
                 */
                try{
                    if(typeof window.actPlayPause === 'function'){
                        window.actPlayPause(false);

                        setTimeout(function(){
                            try{
                                if(typeof window.actPlayPause === 'function'){
                                    window.actPlayPause(true);
                                }
                            }catch(e){}

                            recovering = false;
                        },700);
                        return;
                    }
                }catch(e){}

                recovering = false;
            }

            window.__splAudioRecoveryCheck = function(){
                try{
                    if(!window.playing){
                        stalledSince = 0;
                        lastPosition = null;
                        return;
                    }

                    var p = window.position;
                    var tr = window.track || '';

                    if(!tr){
                        stalledSince = 0;
                        lastPosition = null;
                        return;
                    }

                    /*
                     * New track: reset the watchdog.
                     */
                    if(tr !== lastTrack){
                        lastTrack = tr;
                        lastPosition = p;
                        stalledSince = 0;
                        return;
                    }

                    if(typeof p !== 'number' || isNaN(p)){
                        return;
                    }

                    /*
                     * Position is advancing normally.
                     */
                    if(lastPosition === null || p > lastPosition){
                        lastPosition = p;
                        stalledSince = 0;
                        return;
                    }

                    /*
                     * Spotify says playing, but position has stopped.
                     */
                    if(stalledSince === 0){
                        stalledSince = now();
                        return;
                    }

                    if(now() - stalledSince >= STALL_MS){
                        stalledSince = 0;
                        recover();
                    }

                }catch(e){}
            };

            /*
             * Check every second.
             */
            window.__splAudioRecoveryTimer = setInterval(
                window.__splAudioRecoveryCheck,
                1000
            );

        })();
    """
}
