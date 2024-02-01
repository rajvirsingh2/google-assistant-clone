package com.example.googleassistantclone.functions


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.graphics.Bitmap
import android.hardware.camera2.*
import android.media.MediaRecorder
import android.media.Ringtone
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Telephony
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googleassistantclone.assistant.AssistantViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ml.quaterion.text2summary.Text2Summary
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.content.Context as Context1

@Suppress("DEPRECATION")
class AssistantFunctions {

    companion object {
        var requestCall = 1
        private var mediaRecorder: MediaRecorder? = null
        private var isRecording = false
        var sendSMS = 2
        var shareFile = 4
        var readSMS = 3
        var shareTextFile = 5
        var readContacts = 6
        var capturePhoto = 7
        var imageIndex: Int = 0
        var requestCodeSelect: Int = 100
        var requestEnableBT = 1000
        var audioFilePath: String? = null
        var questions: List<String> = listOf(
            "What would you name your boat if you had one?",
            "What is the closest thing to magic?",
            "What do you think about me?",
            "What will finally break the internet?",
            "What is the most useless talent one could have?"
        )

        val imageDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/Assistant"

        @Suppress("DEPRECATION")
        val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        fun speak(
            message: String,
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
            assistantViewModel.sendMessageToDatabase(keeper, message)
        }

        fun getDate(
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            val calendar = Calendar.getInstance()
            val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
            val splitDate = formattedDate.split("", "").toTypedArray()
            val date = splitDate[1].trim { it <= ' ' }
            speak(
                "The date is $date",
                textToSpeech,
                assistantViewModel,
                keeper
            )
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime(
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm:ss")
            val time = format.format(calendar.time)
            speak(
                "The date is $time",
                textToSpeech,
                assistantViewModel,
                keeper
            )
        }

        fun openFacebook(activity: Activity) {
            val intent = activity.packageManager.getLaunchIntentForPackage("com.facebook.katana")
            intent.let {
                activity.startActivity(it)
            }
        }

        fun openMaps(activity: Activity) {
            val intent =
                activity.packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")
            intent.let {
                activity.startActivity(it)
            }
        }

        fun openGoogle(activity: Activity) {
            val intent =
                activity.packageManager.getLaunchIntentForPackage("com.google.android.googlequicksearchbox")
            intent.let {
                activity.startActivity(it)
            }
        }

        fun openYoutube(activity: Activity) {
            val intent =
                activity.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
            intent.let {
                activity.startActivity(it)
            }
        }

        fun openWhatsAPP(activity: Activity) {
            val intent = activity.packageManager.getLaunchIntentForPackage("com.whatsapp")
            intent.let {
                activity.startActivity(it)
            }
        }

        fun sendWhatsAppMessageToContact(activity: Activity,
                                         textToSpeech: TextToSpeech,
                                         assistantViewModel: AssistantViewModel,
                                         keeper: String) {
            openWhatsAPP(activity)
            openWhatsAppToContact(activity, keeper)
            speak(
                "Opening WhatsApp chat with $keeper",
                textToSpeech,
                assistantViewModel,
                keeper
            )
        }

        fun openWhatsAppToContact(activity: Activity, keeper: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.`package` = "com.whatsapp"
            intent.data = Uri.parse("whatsapp://send?phone=$keeper")
            activity.startActivity(intent)
        }

        fun openGmail(activity: Activity) {
            val intent = activity.packageManager.getLaunchIntentForPackage("com.google.android.gm")
            intent.let {
                activity.startActivity(it)
            }
        }

        fun openMessages(activity: Activity, context: Context1) {
            val intent = activity.packageManager.getLaunchIntentForPackage(
                Telephony.Sms.getDefaultSmsPackage(context)
            )
            intent.let {
                activity.startActivity(it)
            }
        }

        @SuppressLint("Recycle")
        fun readSMS(
            activity: Activity,
            context: Context1,
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_SMS),
                    readSMS
                )
            } else {
                val cursor = activity.contentResolver.query(
                    Uri.parse("content://sms"),
                    null,
                    null,
                    null
                )
                cursor!!.moveToFirst()
                speak(
                    "Your last message was ${cursor.getString(12)}",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }
        }

        fun shareAFile(activity: Activity, context: Context1) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    shareFile
                )
            } else {
                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                val myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
                myFileIntent.type = "application/pdf"
                activity.startActivityForResult(myFileIntent, requestCodeSelect)
            }
        }

        fun shareATextMessage(
            activity: Activity,
            context: Context1,
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    shareTextFile
                )
            } else {

                try {

                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())
                    val message = keeper.split("that").toTypedArray()[1]
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                    activity.startActivity(Intent.createChooser(shareIntent, "Share Text"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    speak(
                        "Something Went Wrong",
                        textToSpeech,
                        assistantViewModel,
                        keeper
                    )
                }
            }
        }

        fun sendSMS(
            activity: Activity,
            context: Context1,
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SEND_SMS
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS
                    ),
                    sendSMS
                )
            } else {
                try {
                    val keeperReplaced = keeper.replace(" ".toRegex(), " ")
                    val number =
                        keeperReplaced.split("o").toTypedArray()[1].split("t").toTypedArray()[0]
                    val message = keeper.split("that").toTypedArray()[1]
                    val mySmsManager = SmsManager.getDefault()
                    mySmsManager.sendTextMessage(
                        number.trim { it <= ' ' },
                        null,
                        message.trim { it <= ' ' },
                        null,
                        null
                    )
                    speak(
                        "Message sent to $number",
                        textToSpeech,
                        assistantViewModel,
                        keeper
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    speak(
                        "Something Went Wrong",
                        textToSpeech,
                        assistantViewModel,
                        keeper
                    )
                }
            }
        }

        fun makeAPhoneCall(
            activity: Activity,
            context: Context1,
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            val keeperSplit = keeper.replace(" ".toRegex(), "")
                .split("dial")
            val number = keeperSplit[1]
            if (number.trim { it <= ' ' }.isNotEmpty()) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) != PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.CALL_PHONE), requestCall
                    )
                } else {
                    try {
                        val dial = "tel:$number"
                        speak("Calling  $number", textToSpeech, assistantViewModel, keeper)
                        activity.startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("error in call", e.message.toString())
                        speak("Something went wrong", textToSpeech, assistantViewModel, keeper)
                    }

                }
            } else {
                speak("Dial Correct Phone Number", textToSpeech, assistantViewModel, keeper)
            }
        }

        fun makeAPhoneCallandRecord(
            activity: Activity,
            context: Context1,
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            val keeperSplit = keeper.replace(" ".toRegex(), "")
                .split("dial")
            val number = keeperSplit[1]
            if (number.trim { it <= ' ' }.isNotEmpty()) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) != PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.CALL_PHONE), requestCall
                    )
                } else {
                    try {
                        val dial = "tel:$number"
                        startRecording()
                        speak("Calling  $number and Recording",
                            textToSpeech,
                            assistantViewModel,
                            keeper)
                        activity.startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("error in call", e.message.toString())
                        speak("Something went wrong", textToSpeech, assistantViewModel, keeper)
                        stopRecording()
                    }
                }
            } else {
                speak("Dial Correct Phone Number", textToSpeech, assistantViewModel, keeper)
            }
        }

        private fun startRecording(){
            if(!isRecording){
                try {
                    mediaRecorder = MediaRecorder()
                    //audio source
                    mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
                    //audio output format
                    mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    //encoder
                    mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    //file path
                    val timestamp:String = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
                    audioFilePath = Environment.getExternalStorageDirectory().absolutePath + "/Recording.$timestamp.3gp"
                    mediaRecorder?.setOutputFile(audioFilePath)
                    //recording audio
                    mediaRecorder?.prepare()
                    mediaRecorder?.start()
                    isRecording = true
                }catch(e: IOException){
                    e.printStackTrace()
                }
            }
        }

        private fun stopRecording(){
            if (!isRecording){
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                isRecording = false
            }
        }

        fun search(
            activity: Activity,
            keeper: String
        ) {
            val uri = Uri.parse("https://www.google.com/search?q=$keeper")
            val googleSearchIntent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(googleSearchIntent)
        }

        @SuppressLint("Range")
        fun callContact(
            activity: Activity,
            textToSpeech: TextToSpeech,
            assistantViewModel: AssistantViewModel,
            keeper: String
        ) {
            var number = ""
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.READ_CONTACTS
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    ),
                    readContacts
                )
            } else {
                val name = keeper.split("call").toTypedArray()[1].trim {
                    it <= ' '
                }
                Log.d("check", name)
                try {
                    val phones: Cursor = activity.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )!!
                    while (phones.moveToNext()) {
                        var contactName: String =
                            phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        contactName = contactName.lowercase(Locale.getDefault())
                        if (contactName.contains(name.lowercase(Locale.getDefault()))) {
                            number =
                                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            Log.d("number", number)
                        }
                    }
                    phones.close()

                    if (number.trim {
                            it <= ' '
                        }.isNotEmpty()) {
                        if (ContextCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.CALL_PHONE
                            ) != PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(Manifest.permission.CALL_PHONE), requestCall
                            )
                        } else {
                            val dial = "tel:$number"
                            speak("Calling now", textToSpeech, assistantViewModel, keeper)
                            activity.startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))

                        }
                    } else {
                        speak("Wrong Contact Name", textToSpeech, assistantViewModel, keeper)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("error in call", e.message.toString())
                    speak("Something went wrong", textToSpeech, assistantViewModel, keeper)
                }
            }
        }

        @SuppressLint("MissingPermission")
        fun turnOnBluetooth(activity: Activity,
                            textToSpeech: TextToSpeech,
                            assistantViewModel: AssistantViewModel,
                            keeper: String){
            if(!bluetoothAdapter.isEnabled){
                speak(
                    "Turning on Bluetooth",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(intent, requestEnableBT)
            }
            else{
                speak(
                    "Bluetooth is Already On",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }
        }

        @SuppressLint("MissingPermission")
        fun turnOffBluetooth(textToSpeech: TextToSpeech,
                             assistantViewModel: AssistantViewModel,
                             keeper: String){
            if(!bluetoothAdapter.isEnabled){
                speak(
                    "Turning off Bluetooth",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
                bluetoothAdapter.disable()
            }
            else{
                speak(
                    "Bluetooth is turned on",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }
        }

        @SuppressLint("MissingPermission")
        fun getAllPairedDevices(textToSpeech: TextToSpeech,
                               assistantViewModel: AssistantViewModel,
                               keeper: String){
            if(!bluetoothAdapter.isEnabled){
                speak(
                    "Paired Devices are ",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
                var text = ""
                var count = ""
                val device: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
                for(devices in device){
                    text += "\nDevices: $count ${devices.name}"
                    count += 1
                }
                speak(text,
                    textToSpeech,
                    assistantViewModel,
                    keeper)
            }
            else{
                speak(
                    "Bluetooth is turned off",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }
        }

        fun turnOnFlash(cameraManager: CameraManager,
                        cameraID: String,
                        textToSpeech: TextToSpeech,
                        assistantViewModel: AssistantViewModel,
                        keeper: String){
            try {
                cameraManager.setTorchMode(cameraID,true)
                speak(
                    "Flash is turned On",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }catch(e: Exception){
                e.printStackTrace()
                speak(
                    "Something Went Wrong",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }
        }

        fun turnOffFlash(cameraManager: CameraManager,
                        cameraID: String,
                        textToSpeech: TextToSpeech,
                        assistantViewModel: AssistantViewModel,
                        keeper: String){
            try {
                cameraManager.setTorchMode(cameraID,false)
                speak(
                    "Flash is turned Off",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }catch(e: Exception){
                e.printStackTrace()
                speak(
                    "Something Went Wrong",
                    textToSpeech,
                    assistantViewModel,
                    keeper
                )
            }
        }

        fun playRingtone(ringtone: Ringtone,
                         textToSpeech: TextToSpeech,
                         assistantViewModel: AssistantViewModel,
                         keeper: String){
            speak(
                "Playing Ringtone",
                textToSpeech,
                assistantViewModel,
                keeper
            )
            ringtone.play()
        }

        fun stopRingtone(ringtone: Ringtone,
                         textToSpeech: TextToSpeech,
                         assistantViewModel: AssistantViewModel,
                         keeper: String){
            speak(
                "Stopped Ringtone",
                textToSpeech,
                assistantViewModel,
                keeper
            )
            ringtone.stop()
        }

        fun readMe(activity: Activity){
            CropImage.startPickImageActivity(activity)
        }

        fun question(textToSpeech: TextToSpeech,
                     assistantViewModel: AssistantViewModel,
                     keeper: String){
            val indexes: List<Int> = listOf(0,1,2,3,4,5)
            val index = indexes.random()
            speak(questions[index],
                textToSpeech,
                assistantViewModel,
                keeper)
        }

        fun clipBoardCopy(clipboardManager: ClipboardManager, textToSpeech: TextToSpeech, assistantViewModel: AssistantViewModel, keeper: String) {
            val data = keeper.split("that").toTypedArray()[0].trim {
                it <= ' '
            }
            if (data.isNotEmpty()) {
                val clipData = ClipData.newPlainText("text", data)
                clipboardManager.setPrimaryClip(clipData)
                speak("data copied to clipboard that is $data", textToSpeech, assistantViewModel, keeper)

            }
        }

        fun clipBoardSpeak(clipboardManager: ClipboardManager, textToSpeech: TextToSpeech, assistantViewModel: AssistantViewModel, keeper: String) {
            val item = clipboardManager.primaryClip!!.getItemAt(0)
            val pasteData = item.text.toString()
            if (pasteData != "") {
                speak("Data stored in last clipboard is $pasteData", textToSpeech, assistantViewModel, keeper)
            } else {
                speak("Clipboard is Empty", textToSpeech, assistantViewModel, keeper)
            }
        }

        fun capturePhoto(activity: Activity, context: Context1, textToSpeech: TextToSpeech, assistantViewModel: AssistantViewModel, keeper: String) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    capturePhoto

                )
            } else {
                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                imageIndex++
                val file = "$imageDirectory$imageIndex.jpg"
                val newFile = File(file)
                try {
                    newFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val outputFileUri = Uri.fromFile(newFile)
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
                activity.startActivity(cameraIntent)
                speak("Photo will be saved to $file", textToSpeech, assistantViewModel, keeper)
            }
        }

        fun captureSelfie(activity: Activity, context: Context1, textToSpeech: TextToSpeech, assistantViewModel: AssistantViewModel, keeper: String) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    capturePhoto
                )
            } else {
                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                imageIndex++
                val file = "$imageDirectory$imageIndex.jpg"
                val newFile = File(file)
                try {
                    newFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val outputFileUri = Uri.fromFile(newFile)

                // Check if the device has a front camera and set the camera ID accordingly
                val frontCameraId = getFrontCameraId(context)
                frontCameraId?.let {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
                    cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1) // 1 for front camera
                    cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1) // 1 for front camera
                    cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    activity.startActivity(cameraIntent)
                    speak("Selfie will be saved to $file", textToSpeech, assistantViewModel, keeper)
                } ?: run {
                    // Handle the case where no front camera is found
                    speak("No front camera found", textToSpeech, assistantViewModel, keeper)
                }
            }
        }

        private fun getFrontCameraId(context: Context1): String? {
            val cameraManager = context.getSystemService(Context1.CAMERA_SERVICE) as CameraManager
            try {
                for (cameraId in cameraManager.cameraIdList) {
                    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                    val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                        return cameraId
                    }
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
            return null
        }


        fun getTextFromBitmap(activity: Activity, bitmap: Bitmap, textToSpeech: TextToSpeech, assistantViewModel: AssistantViewModel, keeper: String) {
            val image = InputImage.fromBitmap(bitmap, 0)
            val options = TextRecognizerOptions.Builder().build()
            val recognizer = TextRecognition.getClient(options)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = visionText.text
                    if (keeper.contains("summarise")) {
                        speak("Reading Image and Summarising it :\n" +
                                summariseText(resultText), textToSpeech, assistantViewModel, keeper)
                    } else {
                        speak("Reading Image $resultText", textToSpeech, assistantViewModel, keeper)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity.applicationContext, "Error ${e.message}", Toast.LENGTH_SHORT).show()

                }
        }

        private fun summariseText(keeper: String): String {
            val summaryRef = kotlin.jvm.internal.Ref.ObjectRef<String>()
            summaryRef.element = Text2Summary.Companion.summarize(keeper, 0.04f)
            return summaryRef.element
        }


        fun motivationalThoughts(textToSpeech: TextToSpeech, assistantViewModel: AssistantViewModel, keeper: String) {

            val horoscopes: List<String> = listOf<String>("If you want to achieve greatness stop asking for permission.",
                "Things work out best for those who make the best of how things work out.",
                "To live a creative life, we must lose our fear of being wrong.",
                "If you are not willing to risk the usual you will have to settle for the ordinary.",
                "Trust because you are willing to accept the risk, not because it's safe or certain.",
                "Take up one idea. Make that one idea your life--think of it, dream of it, live on that idea. Let the brain, muscles, " +
                        "nerves, every part of your body, be full of that idea, and just leave" +
                        " every other idea alone. This is the way to success.",
                "All our dreams can come true if we have the courage to pursue them.",
                "Good things come to people who wait, but better things come to those who go out and get them.",
                "If you do what you always did, you will get what you always got.",
                "Success is walking from failure to failure with no loss of enthusiasm.",
                "Just when the caterpillar thought the world was ending, he turned into a butterfly.")
            val indexes: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            val index = indexes.random()
            speak(horoscopes[index], textToSpeech, assistantViewModel, keeper)
        }

        fun joke(textToSpeech: TextToSpeech, assistantViewModel: AssistantViewModel, keeper: String) {
            val jokes: List<String> = listOf<String>("That bizarre moment when you pick up your car from the garage and you realize that the breaks are still not working, but they made your horn louder.\n",
                " I had a dream where an evil queen forced me to eat a gigantic marshmallow. When I woke up, my pillow was gone.\n",
                "Yesterday I learnt that 20 piranhas can strip all flesh off a man within 15 minutes. - Unfortunately, I also lost my job at the local swimming pool.\n"
            )
            val indexes: List<Int> = listOf(0, 1, 2)
            val index = indexes.random()
            speak(jokes[index], textToSpeech, assistantViewModel, keeper)
        }

    }
}