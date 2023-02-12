package com.example.firstrobot

import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder

class MyChatbot(qiContext: QiContext) : BaseChatbot(qiContext) {

    private val TAG: String = "EslamMyChatbot"
    private val listGreeting =
        listOf("hello", "hi", "good morning", "good afternoon", "good evening")

    override fun replyTo(phrase: Phrase?, locale: Locale?): StandardReplyReaction {

        return if (listGreeting.contains(phrase?.text)) {
            StandardReplyReaction(MyChatbotReaction(qiContext, "Hello you"), ReplyPriority.NORMAL)
        } else {
            StandardReplyReaction(
                MyChatbotReaction(qiContext, "I can just greet you"),
                ReplyPriority.FALLBACK
            )


        }
    }

    override fun acknowledgeHeard(phrase: Phrase?, locale: Locale?) {
        super.acknowledgeHeard(phrase, locale)

        Log.i(TAG, "Last phrase heard by the robot and whose chosen answer is not mine: ${phrase?.text}")

    }

    override fun acknowledgeSaid(phrase: Phrase?, locale: Locale?) {
        super.acknowledgeSaid(phrase, locale)

        Log.i(TAG, "Another chatbot answered: ${phrase?.text}")
    }


}