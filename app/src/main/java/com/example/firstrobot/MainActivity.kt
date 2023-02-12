package com.example.firstrobot

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.airbnb.lottie.model.content.ShapeFill
import com.airbnb.lottie.model.content.ShapeGroup
import com.aldebaran.qi.Consumer
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.Qi
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.conversation.BodyLanguageOption
import com.aldebaran.qi.sdk.`object`.conversation.ListenResult
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.PhraseSet
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.conversation.Topic
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.ExplorationMapBuilder
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.LookAtBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy



class MainActivity : RobotActivity(), RobotLifecycleCallbacks {
    private lateinit var buildSayHello: Say
    private lateinit var animationHello: Animation
    private val TAG: String = "Eslam"
    private lateinit var textView: TextView
    private var _qiContext : QiContext? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        QiSDK.register(this, this)

        val build = QiChatbotBuilder.with(_qiContext).build()
        build.globalRecommendations(10)


        textView = findViewById(R.id.text_view)

        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)

        val shapeDrawable = ShapeDrawable()
        shapeDrawable.shape = RectShape()


        val phraseSet : PhraseSet = PhraseSetBuilder.with(_qiContext).withTexts("hello","hi").build()
        val phrase : Phrase = Phrase("Hi")
        val listen = ListenResult(phraseSet,phrase)

    }


    override fun onDestroy() {
        QiSDK.unregister(this, this)
        super.onDestroy()
    }


    override fun onRobotFocusGained(qiContext: QiContext?) {
        this._qiContext = qiContext
        Log.i(TAG, "onRobotFocusGained: ")

        pepperSayHello(qiContext)
        animationHello =
            AnimationBuilder.with(qiContext).withResources(R.raw.animation_hello).build()

        animationHello.async()

    }

    private fun pepperSayHello(qiContext: QiContext?) {
         val sayHelloEslam = SayBuilder.with(qiContext).withText("Hello Eslam")

        SayBuilder.with(qiContext).withPhrase(Phrase("Hello this Phrase"))
        buildSayHello = sayHelloEslam.build()

        // to check is it function completed or not
        buildSayHello.async().run().thenConsume {
            if(it.isSuccess){
                Log.i(TAG, "pepperSayHello: SUCCESS")
            }else if(it.hasError()){
                Log.i(TAG, "pepperSayHello: ERROR")
            }
        }



        // this another way to back to UI thread QiSDK-based
        buildSayHello.async().run().andThenConsume(Qi.onUiThread(Consumer {
            textView.text = ""
        })).andThenCompose {

            val sayPepper = SayBuilder.with(qiContext).withText("I'm pepper").build()
           return@andThenCompose sayPepper.async().run()
        }


        buildSayHello.async().run()


        ChatBuilder.with(qiContext)
    }


    override fun onRobotFocusLost() {
        _qiContext = null
        Log.i(TAG, "onRobotFocusLost: we lost Pepper")

        //error because this function run on background thread
        textView.text = "we lost pepper"


        // The Future will continue its execution.
//        val humansAroundFuture: Future<List<Human>> = humanAwareness.async().humansAround

        //Because listeners can be triggered without robot focus, you should remove all listeners
//        LookAtBuilder.with(qiContext).build().removeAllOnPolicyChangedListeners()
        buildSayHello.removeAllOnStartedListeners()

    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.i(TAG, "onRobotFocusRefused: ")

        Log.d(TAG, "onRobotFocusRefused: $reason")


//        back to UI thread android-based solution
        runOnUiThread {
            textView.text = "Refused"
        }
    }

    private fun chatWithPepper(){
        val qiChat = QiChatbotBuilder.with(_qiContext).withTopic().build()
        val chat = ChatBuilder.with(_qiContext).withChatbot(qiChat).build()
        chat.async().run()

        chat.listeningBodyLanguage = BodyLanguageOption.DISABLED

        val listening = chat.listening
        val hearing = chat.hearing

        chat.addOnListeningChangedListener {

        }
    }

    private fun listenPepper(){


        val locale = Locale(Language.ENGLISH,Region.GERMANY)
        val phraseSet = PhraseSetBuilder.with(_qiContext).withTexts("Any thing").build()
        val listen = ListenBuilder.with(_qiContext).withLocale(locale).withPhraseSet(phraseSet).build()
        val listenResult = listen.run()

        Log.i(TAG, "listenPepper: ${listenResult.heardPhrase.text}")
        listenResult.matchedPhraseSet

    }


    private fun doAnimation(){

        val animation = AnimationBuilder.with(_qiContext).withResources(R.raw.animation_hello).build()

        val animate = AnimateBuilder.with(_qiContext).withAnimation(animation).build()
        animate.async().run()


        animate.addOnLabelReachedListener { label, time ->
            // Called when a label is reached.
        }

        val explorationMap = ExplorationMapBuilder.with(_qiContext)
    }


}