package com.chanho.loveday

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chanho.loveday.application.MyApplication
import com.chanho.loveday.databinding.FragmentMainBinding
import com.theartofdev.edmodo.cropper.CropImage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMainBinding
    var whoImage = "boy"

    private val MEDIA_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        eventProfileBtn()
        eventSettingBtn()
        setIngday()
        setProfile()

        return binding.root
    }

    private fun setIngday() {
        val ingDay = MyApplication.prefs.getLong("ingDay", 0)
        binding.ingText.text = ingDay.toString() + "일째"
    }

    private fun setProfile() {
        val boyImagePath = MyApplication.prefs.getString("boyImagePath", "")
        val girlImagePath = MyApplication.prefs.getString("girlImagePath", "")

        if (boyImagePath != "") {
            Handler().postDelayed({
                GlideApp.with(requireActivity())
                    .load(boyImagePath)
                    .into(binding.mainBoyImage)
            }, 10)
        }

        if (girlImagePath != "") {
            Handler().postDelayed({
                GlideApp.with(requireActivity())
                    .load(girlImagePath)
                    .into(binding.mainGirlImage)
            }, 10)
        }
    }

    private fun eventProfileBtn() {
        binding.leftButton.setOnClickListener {
            if (areMediaPermissionsGranted()) {
                val photoSave = AlertDialog.Builder(requireContext())
                var btnAction: DialogInterface.OnClickListener?

                btnAction = DialogInterface.OnClickListener { _, _ ->
                    whoImage = "boy"
                    pickImage()
                }
                photoSave.setPositiveButton("남자친구 프로필 사진 변경", btnAction)

                btnAction = DialogInterface.OnClickListener { _, _ ->
                    whoImage = "girl"
                    pickImage()
                }
                photoSave.setNegativeButton("여자친구 프로필 사진 변경", btnAction)

                btnAction = DialogInterface.OnClickListener { _, _ ->

                }

                photoSave.setNeutralButton("취소", btnAction)

                photoSave.show()

            } else {
                requestMediaPermissions()
            }
        }
    }

    private fun eventSettingBtn() {
        binding.rightButton.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun cropImage(uri: Uri) {
        val cropIntent = CropImage.activity(uri)
            .setAspectRatio(1, 1) // 원하는 크롭 비율 설정
            .getIntent(requireContext())
        cropImageLauncher.launch(cropIntent)
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    cropImage(uri)
                }
            }
        }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val cropResult = CropImage.getActivityResult(data)

                cropResult.uri?.let { uri ->
                    Log.e("IMAGE", "이미지 선택: $uri")
                    if (whoImage == "boy") {
                        Handler().postDelayed({
                            GlideApp.with(requireActivity())
                                .load(uri)
                                .into(binding.mainBoyImage)
                        }, 10)

                        MyApplication.prefs.setString("boyImagePath", uri.toString())
                    } else {
                        Handler().postDelayed({
                            GlideApp.with(requireActivity())
                                .load(uri)
                                .into(binding.mainGirlImage)
                        }, 10)
                        MyApplication.prefs.setString("girlImagePath", uri.toString())
                    }
                }
            } else if (result.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.data?.let { CropImage.getActivityResult(it).error }
                Toast.makeText(requireContext(), error?.message, Toast.LENGTH_SHORT).show()
            }
        }

    private fun areMediaPermissionsGranted(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMediaPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            MEDIA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MEDIA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우에 대한 처리
            } else {
                // 권한이 거부된 경우에 대한 처리
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}