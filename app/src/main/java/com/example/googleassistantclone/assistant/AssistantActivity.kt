package com.example.googleassistantclone.assistant

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
import android.speech.tts.TextToSpeech.SUCCESS
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.googleassistantclone.R
import com.example.googleassistantclone.UI_Utils.Utils
import com.example.googleassistantclone.UI_Utils.Utils.LogKeeper
import com.example.googleassistantclone.UI_Utils.Utils.LogSre
import com.example.googleassistantclone.UI_Utils.Utils.LogTTS
import com.example.googleassistantclone.data.AssistantDatabase
import com.example.googleassistantclone.databinding.ActivityAssistantBinding
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.callContact
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.capturePhoto
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.captureSelfie
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.clipBoardCopy
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.clipBoardSpeak
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.getAllPairedDevices
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.getDate
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.getTextFromBitmap
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.getTime
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.joke
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.makeAPhoneCall
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.makeAPhoneCallandRecord
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.motivationalThoughts
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.openFacebook
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.openGmail
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.openGoogle
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.openMaps
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.openMessages
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.openWhatsAPP
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.openYoutube
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.playRingtone
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.question
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.readContacts
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.readMe
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.readSMS
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.requestCall
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.requestCodeSelect
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.requestEnableBT
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.search
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.sendSMS
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.sendWhatsAppMessageToContact
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.shareAFile
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.shareATextMessage
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.shareFile
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.shareTextFile
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.speak
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.stopRingtone
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.turnOffBluetooth
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.turnOffFlash
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.turnOnBluetooth
import com.example.googleassistantclone.functions.AssistantFunctions.Companion.turnOnFlash
import com.example.googleassistantclone.functions.GoogleLensActivity
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.FileNotFoundException
import java.util.Locale

@Suppress("DEPRECATION")
class AssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssistantBinding
    private lateinit var assistantViewModel: AssistantViewModel
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizer: Intent
    private lateinit var keeper: String
    private lateinit var cameraManager: CameraManager
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var cameraID: String
    private lateinit var ringtone: Ringtone
    private lateinit var imgUri: Uri

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assistant)
        Utils.setCustomActionBar(supportActionBar, this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_assistant)
        if(Settings.System.canWrite(this)){
            ringtone = RingtoneManager.getRingtone(applicationContext, RingtoneManager.
            getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        }else{
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package: "+ this.packageName)
            startActivity(intent)
        }

        //Initialized the view model here!!!
        val application = requireNotNull(this).application
        val dataSource = AssistantDatabase.getInstance(this).assistantDao
        val viewModelFactory = AssistantViewModelFactory(dataSource, application)
        assistantViewModel = ViewModelProvider(this,viewModelFactory)[AssistantViewModel::class.java]
        val adapter = AssistantAdapter()
        binding.recyclerView.adapter = adapter
        assistantViewModel.message.observe(this
        ) {
            it?.let {
                adapter.data = it
            }
        }
        binding.lifecycleOwner = this
        if(savedInstanceState == null){
            binding.assistantLinearLayout.visibility = View.INVISIBLE
            // use of tree to save the conversation

            val viewTreeObserver: ViewTreeObserver = binding.assistantLinearLayout.viewTreeObserver
            if(viewTreeObserver.isAlive){
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        circularRevealActivity()
                        binding.assistantLinearLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                })
            }

        }
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraID = cameraManager.cameraIdList[0]
            // 0 -> back camera
            // 1 -> front camera
        }
        catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        textToSpeech = TextToSpeech(this){
            status ->
            if(status== SUCCESS){
                val result: Int = textToSpeech.setLanguage(Locale.ENGLISH)
                if(result == LANG_NOT_SUPPORTED || result == LANG_NOT_SUPPORTED){
                    Log.i(LogTTS,"Language Not Supported")
                }
                else{
                    Log.i(LogTTS,"Language is Supported")
                }
            }
            else{
                Log.i(LogTTS,"Initialization of Text to Speech Failed")
            }
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
                Log.i(LogSre,"Started")
            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {
                Log.d(LogSre,"Ended")
            }

            override fun onError(error: Int) {
                Log.i(LogSre,error.toString())
            }

            override fun onResults(results: Bundle?) {
                val data = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if(data != null){
                    keeper = data[0]
                    Log.d(LogKeeper,keeper)
                    when{
                        keeper.contains("call and record") -> makeAPhoneCallandRecord(this@AssistantActivity,applicationContext,textToSpeech,assistantViewModel, keeper)
                        keeper.contains("thanks") -> speak("Its my job , let me know if there is something else", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("take a selfie") -> captureSelfie(this@AssistantActivity, applicationContext, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("search") -> search(this@AssistantActivity,keeper)
                        keeper.contains("welcome") -> speak("Its my pleasure to help you out", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("go out with me") ||  keeper.contains("club") ||  keeper.contains("coffee") ||  keeper.contains("dance") ||  keeper.contains("love")  ||  keeper.contains("ok") -> speak("Yes , Of-course", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("clear") ||  keeper.contains("delete")-> assistantViewModel.onClear()
                        keeper.contains("date") -> getDate(textToSpeech, assistantViewModel, keeper)
                        keeper.contains("time") -> getTime(textToSpeech, assistantViewModel, keeper)
                        keeper.contains("dial") -> makeAPhoneCall(this@AssistantActivity, applicationContext, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("send sms") || keeper.contains("send SMS") -> sendSMS(this@AssistantActivity, applicationContext, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("read my last sms") || keeper.contains("read my last SMS") || keeper.contains("read my SMS") -> readSMS(this@AssistantActivity, applicationContext, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("open Gmail") || keeper.contains("Gmail") ||keeper.contains("gmail") ||keeper.contains("mail") -> openGmail(this@AssistantActivity)
                        keeper.contains("open Maps") || keeper.contains("open maps")  || keeper.contains("maps")-> openMaps(this@AssistantActivity)
                        keeper.contains("open Google") || keeper.contains("open Google") || keeper.contains("open Chrome") -> openGoogle(this@AssistantActivity)
                        keeper.contains("open Whatsapp") || keeper.contains("open WhatsApp") -> openWhatsAPP(this@AssistantActivity)
                        keeper.contains("send WhatsApp message") -> sendWhatsAppMessageToContact(this@AssistantActivity, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("open facebook") || keeper.contains("open Facebook") || keeper.contains("open Face") || keeper.contains("open Facebook") -> openFacebook(this@AssistantActivity)
                        keeper.contains("open messages") -> openMessages(this@AssistantActivity, applicationContext)
                        keeper.contains("how to use google assistant") || keeper.contains("google assistant") || keeper.contains("how to use") || keeper.contains("can I do") || keeper.contains("what can I do") || keeper.contains("Google assistant") || keeper.contains("can")-> speak("Try some Commands : open whatsapp , open facebook , tell me a joke , hi , hello , explore , google lens", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("open youtube") || keeper.contains("open YouTube") -> openYoutube(this@AssistantActivity)
                        keeper.contains("share file") -> shareAFile(this@AssistantActivity, applicationContext)
                        keeper.contains("share a text message") -> shareATextMessage(this@AssistantActivity, applicationContext, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("call") -> callContact(this@AssistantActivity, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("turn on bluetooth") || keeper.contains("turn on Bluetooth") -> turnOnBluetooth(this@AssistantActivity, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("turn off bluetooth") || keeper.contains("turn off Bluetooth") -> turnOffBluetooth(textToSpeech, assistantViewModel, keeper)
                        keeper.contains("get bluetooth devices") -> getAllPairedDevices(textToSpeech, assistantViewModel, keeper)
                        keeper.contains("turn on flash") -> turnOnFlash(cameraManager, cameraID, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("turn off flash") -> turnOffFlash(cameraManager, cameraID, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("copy to clipboard") ||   keeper.contains("copy") -> clipBoardCopy(clipboardManager, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("read last clipboard")|| keeper.contains("read my clipboard")|| keeper.contains("read clipboard")-> clipBoardSpeak(clipboardManager, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("capture photo") -> capturePhoto(this@AssistantActivity, applicationContext, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("play ringtone") ||  keeper.contains("play something") ||  keeper.contains("play")||  keeper.contains("song")-> playRingtone(ringtone, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("stop ringtone") || keeper.contains("stop playing") || keeper.contains("stop music") || keeper.contains("stop") || keeper.contains("sto") -> stopRingtone(ringtone, textToSpeech, assistantViewModel, keeper)
                        keeper.contains("read me") -> readMe(this@AssistantActivity)
                        keeper.contains("weather") ||
                                keeper.contains("explore")||
                                keeper.contains("Explore")||
                                keeper.contains("Commands")||
                                keeper.contains("commands")-> startActivity(Intent(this@AssistantActivity, ExploreActivity::class.java))
                        keeper.contains("lens")||keeper.contains("Lens")||keeper.contains("len")-> startActivity(Intent(this@AssistantActivity, GoogleLensActivity::class.java))
                        keeper.contains("motivate") || keeper.contains("any thoughts") || keeper.contains("motivational thoughts") || keeper.contains("motivational") -> motivationalThoughts(textToSpeech, assistantViewModel, keeper)
                        keeper.contains("joke") -> joke(textToSpeech, assistantViewModel, keeper)
                        keeper.contains("question") -> question(textToSpeech, assistantViewModel, keeper)
                        keeper.contains("haha") || keeper.contains("hehe") -> speak("I know , I am funny", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("are you married") ||   keeper.contains("married") ||   keeper.contains("marry") -> speak("Yes to my work !", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("boat") || keeper.contains("real magic")
                                || keeper.contains("magic") || keeper.contains("useless talent")
                                || keeper.contains("smelling place") || keeper.contains("smelling ") ->
                            speak("You are funny haha", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("what is your name") || keeper.contains("your name")
                                || keeper.contains("what do you call your self") ->
                            speak("I am Google Assistant at  your service", textToSpeech, assistantViewModel, keeper)
                        keeper.contains("hello") || keeper.contains("hi") || keeper.contains("hey") || keeper.contains("hay")
                        -> speak("Hello , how can I help you ?", textToSpeech, assistantViewModel, keeper)

                        else -> speak("Please try another comment like  what is your name , call someone , read my sms , open google lens , explore", textToSpeech, assistantViewModel, keeper)
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {

            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }

        })

        binding.assistantMic.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                }

                MotionEvent.ACTION_DOWN -> {
                    textToSpeech.stop()
                    speechRecognizer.startListening(recognizer)
                }
            }
            false

        }
        checkSpeechRecognizerAvailable()

    }

    private fun checkSpeechRecognizerAvailable() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.d(LogSre, "yes")
        } else {
            Log.d(LogSre, "false")
        }

    }

    private fun circularRevealActivity() {
        val cx: Int = binding.assistantLinearLayout.right-getDips(44)
        val cy: Int = binding.assistantLinearLayout.bottom-getDips(44)
        val finalRadius: Int = Math.max(
            binding.assistantLinearLayout.width,
            binding.assistantLinearLayout.height
        )
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            binding.assistantLinearLayout,
            cx,
            cy,
            0f,
            finalRadius.toFloat()
        )
        var animationTime = 100
        circularReveal.duration = animationTime.toLong()
        binding.assistantLinearLayout.visibility = View.VISIBLE
    }

    private fun getDips(i: Int): Int {
        val resources: Resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            i.toFloat(),
            resources.displayMetrics
        ). toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
        speechRecognizer.cancel()
        speechRecognizer.destroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            val cx: Int = binding.assistantLinearLayout.right-getDips(44)
            val cy: Int = binding.assistantLinearLayout.bottom-getDips(44)
            val finalRadius: Int = Math.max(
                binding.assistantLinearLayout.width,
                binding.assistantLinearLayout.height
            )
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.assistantLinearLayout,
                cx,
                cy,
                0f,
                finalRadius.toFloat()
            )
            circularReveal.addListener(object: Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.assistantLinearLayout.visibility = View.GONE
                    finish()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            })
            var animationTime = 100
            circularReveal.duration = animationTime.toLong()
            circularReveal.start()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestCodeSelect && resultCode == RESULT_OK){
            val filePath = data!!.data!!.path
            val file = filePath?.let { File(it) }
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "application/pdf"
            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://$file"))
            startActivity(Intent.createChooser(intentShare, "Share the file"))
        }
        if(requestCode == requestEnableBT){
            if(resultCode == RESULT_OK){
                speak("Bluetooth is ON", textToSpeech, assistantViewModel,keeper)
            }else{
                speak("Bluetooth is OFF", textToSpeech, assistantViewModel,keeper)
            }
        }
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            val imageUri = CropImage.getPickImageResultUri(this,data)
            imgUri = imageUri
            startCrop(imageUri)
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){
                imgUri = result.uri
                try {
                    val inputStream = contentResolver.openInputStream(imgUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    getTextFromBitmap(this, bitmap,textToSpeech,assistantViewModel,keeper)
                }catch (e: FileNotFoundException){
                    e.printStackTrace()
                }
            }
            Toast.makeText(this,"Image Captured Succesfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCrop(imageUri: Uri?) {
        CropImage.activity(imageUri).setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .start(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == requestCall){
            if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
                makeAPhoneCall(this, applicationContext,textToSpeech,assistantViewModel,keeper)
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else if(requestCode == readSMS) {
            if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
                readSMS(this, applicationContext,textToSpeech,assistantViewModel,keeper)
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else if(requestCode == shareFile) {
            if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
                shareAFile(this, applicationContext)
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else if(requestCode == shareTextFile) {
            if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
                shareATextMessage(this, applicationContext,textToSpeech,assistantViewModel,keeper)
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else if(requestCode == readContacts) {
            if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
                callContact(this,textToSpeech,assistantViewModel,keeper)
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else if(requestCode == capturePhoto) {
            if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
                capturePhoto(this, applicationContext,textToSpeech,assistantViewModel,keeper)
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}