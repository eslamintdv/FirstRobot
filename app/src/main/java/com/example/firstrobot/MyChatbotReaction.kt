package com.example.firstrobot

import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.BaseChatbotReaction
import com.aldebaran.qi.sdk.`object`.conversation.SpeechEngine
import com.aldebaran.qi.sdk.builder.SayBuilder
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException

class MyChatbotReaction(qiContext: QiContext, private val answer: String) :
    BaseChatbotReaction(qiContext) {

    private var sayFuture: Future<Void>? = null
    private val TAG = "EslamChatbotReaction"

    override fun runWith(speechEngine: SpeechEngine?) {

        //Can not executed here
        SayBuilder.with(qiContext).withText(answer).build()

        //this will executed
        val say = SayBuilder.with(speechEngine).withText(answer).build()
        sayFuture = say.async().run()
        try {
            sayFuture?.get()
        } catch (e: ExecutionException) {
            Log.e(TAG, "Error during Say: ", e)
        } catch (e: CancellationException) {
            Log.i(TAG, "Interruption during Say: $e")
        }


    }

    override fun stop() {
        sayFuture?.cancel(true)
    }
}