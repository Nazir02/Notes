package com.example.notes.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.databinding.FragmentAddBinding
import com.example.notes.model.NoteModel
import com.example.notes.vm.AddViewModel
import com.example.notes.vm.DetailViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: FragmentAddBinding
    private var currentPhotoFirst: String = ""
    private var currentPhotoSecond: String = ""

    companion object {
        lateinit var currentNote: NoteModel
        var Image_Pick_Code = 1000
        const val Permission_Code = 1002
        const val PERMISSION_TAKE_PHOTO = 1
        var day = 0
        var month = 0
        var year = 0
        var savedDay = 0
        var savemMonth = ""
        var saveYear = 0
        var updateLat: Double = 0.0
        var updateLng: Double = 0.0
        var mapsAddress = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        try {
            currentNote = arguments?.getSerializable("note") as NoteModel
        } catch (e: Exception) {
        }
        binding.locationInput.setStartIconOnClickListener {
            onClickLocation()
        }
        binding.ImegViewAdd1.setOnClickListener {
            Image_Pick_Code = 1
            onClickImageView()
        }
        binding.ImegViewAdd2.setOnClickListener {
            Image_Pick_Code = 0
            onClickImageView()
        }
        pickDate()

        return binding.root
    }

    private fun onClickImageView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                val peremission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        peremission,
                        Permission_Code)
            } else {
                showPictureDialog()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                val peremission1 = arrayOf(Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        peremission1,
                        PERMISSION_TAKE_PHOTO
                )
            }
        } else {
            showPictureDialog()
        }
    }

    private fun showDeleteDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Удалить запис")
        alertDialogBuilder.setMessage("Точно хотите уалть")
        alertDialogBuilder.setPositiveButton("ok") { _, _ ->
            delete()
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            activity?.onBackPressed()
        }
        alertDialogBuilder.show()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        pictureDialog.setTitle("Выберите действие")
        val pictureDialogItems =
                if (ImegViewAdd1.drawable != null && Image_Pick_Code == 1 && ImegViewAdd2.drawable == null) {
                    arrayOf("Выбрать изображение из галереи", "Захват фото с камеры", "Удалить", "Открыт")
                } else if (ImegViewAdd2.drawable != null && Image_Pick_Code == 0 && ImegViewAdd1.drawable == null) {
                    arrayOf("Выбрать изображение из галереи", "Захват фото с камеры", "Удалить", "Открыт")
                } else if (ImegViewAdd2.drawable == null && ImegViewAdd1.drawable == null) {
                    arrayOf("Выбрать изображение из галереи", "Захват фото с камеры")
                } else if (ImegViewAdd1.drawable != null && ImegViewAdd2.drawable != null) {
                    arrayOf("Выбрать изображение из галереи", "Захват фото с камеры", "Удалить", "Открыт")
                } else {
                    arrayOf("Выбрать изображение из галереи", "Захват фото с камеры")
                }
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> pickImageFromGallery()
                1 -> takePhotoFromCamera()
                2 -> deletePhoto()
                3 -> fullScreenPhoto()
            }
        }
        pictureDialog.show()
    }

    private fun fullScreenPhoto() {
        val intent = Intent(requireContext(), FullScreenPhoto::class.java)
        when {
            currentPhotoFirst != "" && Image_Pick_Code == 1 && ImegViewAdd1.drawable != null -> {
                intent.putExtra("photo", currentPhotoFirst)
                startActivity(intent)
            }
            currentPhotoFirst == "" && Image_Pick_Code == 1 && ImegViewAdd1.drawable != null -> {
                intent.putExtra("photo", currentNote.filePhoto)
                startActivity(intent)
            }
            currentPhotoSecond == "" && Image_Pick_Code == 0 && ImegViewAdd2.drawable != null -> {
                intent.putExtra("photo", currentNote.filePhoto1)
                startActivity(intent)
            }
            currentPhotoSecond != "" && Image_Pick_Code == 0 && ImegViewAdd2.drawable != null -> {
                intent.putExtra("photo", currentPhotoSecond)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(), "com.example.android.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    getResult.launch(takePictureIntent)
                    if (Image_Pick_Code == 1) {
                        ImegViewAdd1.setImageURI(photoURI)
                    } else if (Image_Pick_Code == 0) {
                        ImegViewAdd2.setImageURI(photoURI)
                    }
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        getResult.launch(intent)
    }

    private fun deletePhoto() {
        if (Image_Pick_Code == 1) {
            deleteFiles(currentNote.filePhoto.toString())
            ImegViewAdd1.setImageURI(null)
            ImegViewAdd1.setBackgroundResource(R.drawable.photo_camera_24)
            currentPhotoFirst = "1"
        } else if (Image_Pick_Code == 0) {
            deleteFiles(currentNote.filePhoto1.toString())
            ImegViewAdd2.setImageURI(null)
            ImegViewAdd2.setBackgroundResource(R.drawable.photo_camera_24)
            currentPhotoSecond = "1"
        }
    }

    private fun deleteFiles(path: String) {
        val file = File(path)
        if (file.exists()) {
            val deleteCmd = "rm -r $path"
            val runtime = Runtime.getRuntime()
            try {
                runtime.exec(deleteCmd)
            } catch (e: IOException) {
                Toast.makeText(requireContext(), "delet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val getResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (it.data != null) {
                        val imageUri = it.data?.data as Uri
                        if (Image_Pick_Code == 1) {
                            ImegViewAdd1.setImageURI(imageUri)
                            currentPhotoFirst = imageUri.toString()
                        } else if (Image_Pick_Code == 0) {
                            ImegViewAdd2.setImageURI(imageUri)
                            currentPhotoSecond = imageUri.toString()
                        }
                    }
                }
            }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        if (MainFragment.clik == 1) {
            (activity as MainActivity).supportActionBar?.title = currentNote.title
            binding.titleTextAdd.setText(currentNote.title)
            activity?.actionBar?.title = binding.titleTextAdd.text
            binding.subTitleTextAdd.setText(currentNote.subTitle)
            binding.dataTime.setText(currentNote.dataAndTime)
            if (MapsFragment.updeta != 1) {
                binding.location.setText((currentNote.addres))
            }
            if (currentPhotoFirst == "") {
                this.binding.ImegViewAdd1.setImageURI(currentNote.filePhoto?.toUri())
            } else {
                this.binding.ImegViewAdd1.setImageURI(currentPhotoFirst.toUri())
            }
            if (currentPhotoSecond == "") {
                this.binding.ImegViewAdd2.setImageURI(currentNote.filePhoto1?.toUri())
            } else {
                this.binding.ImegViewAdd2.setImageURI(currentPhotoSecond.toUri())
            }

        }
    }


    override fun onResume() {
        super.onResume()
        if (currentPhotoFirst == "" && MainFragment.clik == 1) {
            this.binding.ImegViewAdd1.setImageURI(currentNote.filePhoto?.toUri())
        } else
            if (currentPhotoFirst != "") {
                this.binding.ImegViewAdd1.setImageURI(currentPhotoFirst.toUri())
            } else {
                this.binding.ImegViewAdd1.setImageURI(currentPhotoFirst.toUri())
            }
        if (currentPhotoSecond == "" && MainFragment.clik == 1) {
            this.binding.ImegViewAdd2.setImageURI(currentNote.filePhoto1?.toUri())
        } else if (currentPhotoSecond != "") {
            this.binding.ImegViewAdd2.setImageURI(currentPhotoSecond.toUri())
        } else {
            this.binding.ImegViewAdd2.setImageURI(currentPhotoSecond.toUri())
        }

        if (MainFragment.clik == 1 && MapsFragment.updeta != 1) {
            binding.location.setText(currentNote.addres)
        } else
            try {
                binding.location.setText(mapsAddress)
            } catch (e: Exception) {
            }

        if (ImegViewAdd1.drawable != null) {
            ImegViewAdd1.setBackgroundResource(R.color.back)
        }
        if (ImegViewAdd2.drawable != null) {
            ImegViewAdd2.setBackgroundResource(R.color.back)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        when (requestCode) {
            Permission_Code -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "dostup est", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "dostup net", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_TAKE_PHOTO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "dostup est", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "dostup net", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
        ).apply {
            if (Image_Pick_Code == 1) {
                currentPhotoFirst = absolutePath
            } else if (Image_Pick_Code == 0) {
                currentPhotoSecond = absolutePath
            }
        }
    }

    private fun getDataTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    private fun pickDate() {
        binding.dataTimeInput.setStartIconOnClickListener {
            getDataTimeCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        when (month) {
            0 -> {
                savemMonth = "Январь"
            }
            1 -> {
                savemMonth = "Февраль"
            }
            2 -> {
                savemMonth = "Март"
            }
            3 -> {
                savemMonth = "Апрель"
            }
            4 -> {
                savemMonth = "Май"
            }
            5 -> {
                savemMonth = "Июнь"
            }
            6 -> {
                savemMonth = "Июль"
            }
            7 -> {
                savemMonth = "Август"
            }
            8 -> {
                savemMonth = "Сентябрь"
            }
            9 -> {
                savemMonth = "Октябрь"
            }
            10 -> {
                savemMonth = "Ноябрь"
            }
            11 -> {
                savemMonth = "Декабрь"
            }
        }
        saveYear = year
        getDataTimeCalendar()
        binding.dataTime.setText("$savedDay $savemMonth $saveYear")
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (MainFragment.clik == 1) {
            inflater.inflate(R.menu.nav_menu, menu)
        } else {
            inflater.inflate(R.menu.nav_menu_add, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.updata -> {
                if (MainFragment.clik != 1) {
                    updata()
                } else {
                    updataDetail()
                }
                true
            }
            R.id.delet -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickLocation() {
        if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MapsFragment.MY_PERMISSION_FINE_LOCATION)
            return
        }
        if (this.isConnected(requireContext()) == false) {
            Toast.makeText(requireContext(), "Включите Интернет", Toast.LENGTH_SHORT
            ).show()
        } else {
            if (isGPSEnabled()) {
                try {
                    val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                    val addressList = geoCoder.getFromLocationName(location.text.toString(), 1)
                    if (addressList.isNotEmpty()) {
                        val address1 = addressList[0] as Address
                        val sb = StringBuilder()
                        sb.append(address1.latitude)
                        sb.append(address1.longitude)
                        val a = address1.latitude
                        val b = address1.longitude
                        updateLat = a
                        updateLng = b
                    }
                } catch (e: Exception) {
                }
                findNavController().navigate(R.id.mapsFragment)
                MapsFragment.addFragment = "1"
            } else {
                Toast.makeText(requireContext(), "Включите геолокацию", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updata() {
        if (binding.titleTextAdd.text.toString() != "") {
            val viewModel = ViewModelProvider(this).get(AddViewModel::class.java)
            viewModel.insert(
                    NoteModel(
                            title = binding.titleTextAdd.text.toString(),
                            subTitle = binding.subTitleTextAdd.text.toString(),
                            filePhoto = currentPhotoFirst,
                            filePhoto1 = currentPhotoSecond,
                            dataAndTime = binding.dataTime.text.toString(),
                            addres = binding.location.text.toString())
            ) {}
            MAIN.navController.navigate(R.id.action_addFragment_to_mainFragment)
        } else {
            Toast.makeText(requireContext(), "Напишите заголовки)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updataDetail() {
        val viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        MapsFragment.updeta = 0
        val ubdetaTitle = this.binding.titleTextAdd.text.toString()
        val ubdetaSubTitle = this.binding.subTitleTextAdd.text.toString()
        val ubdetaDataTime = binding.dataTime.text.toString()
        val photoFirst: String = if (currentPhotoFirst == "") {
            val a = currentNote.filePhoto
            a.toString()
        } else {
            currentPhotoFirst
        }
        val photoSecond: String = if (currentPhotoSecond == "") {
            currentNote.filePhoto1.toString()
        } else {
            currentPhotoSecond
        }
        viewModel.ubdate(
                NoteModel(
                        id = currentNote.id,
                        ubdetaTitle,
                        subTitle = ubdetaSubTitle,
                        filePhoto = photoFirst,
                        filePhoto1 = photoSecond,
                        dataAndTime = ubdetaDataTime,
                        addres = binding.location.text.toString())
        ) {}
        MAIN.navController.navigate(R.id.action_addFragment_to_mainFragment)
    }

    private fun delete() {
        val viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        viewModel.delet(currentNote) {}
        MAIN.navController.navigate(R.id.action_addFragment_to_mainFragment)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun isConnectedNewApi(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.getNetworkCapabilities(cm.activeNetwork)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        return capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    fun isConnectedOld(context: Context): Boolean? {
        val connManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        return networkInfo?.isConnected
    }

    private fun isConnected(context: Context): Boolean? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isConnectedNewApi(context)
        } else {
            isConnectedOld(context)
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager: LocationManager =
                (requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapsAddress = ""
        MainFragment.clik = 0
    }
}




