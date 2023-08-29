package com.chanho.loveday

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.HashMap

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

    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        eventProfileBtn(view)
        eventSettingBtn(view)

        setIngday(view)

        return view
    }

    private fun setIngday(view: View) {
        preferences = requireActivity().getSharedPreferences("setDDay", Context.MODE_PRIVATE)
        val ingDay = preferences?.getLong("ingDay", 0)
        val ingText = view.findViewById<TextView>(R.id.ingText)
        ingText.text = ingDay.toString() + "일째"
    }

    private fun eventProfileBtn(view: View) {
        val leftButton = view.findViewById<ImageButton>(R.id.leftButton)

        leftButton.setOnClickListener {
            Log.v("test log", "왼쪽 로그")
        }
    }

    private fun eventSettingBtn(view: View) {
        val rightButton = view.findViewById<ImageButton>(R.id.rightButton)
        var leftButton = view.findViewById<ImageButton>(R.id.leftButton)

        rightButton.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }

        leftButton.setOnClickListener {
            val photoSave = AlertDialog.Builder(requireContext())
            var btnAction: DialogInterface.OnClickListener?

            btnAction = DialogInterface.OnClickListener { _, _ ->
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(requireActivity())
            }
            photoSave.setPositiveButton("남자친구 프로필 사진 변경", btnAction)

            btnAction = DialogInterface.OnClickListener { _, _ ->

            }
            photoSave.setNegativeButton("여자친구 프로필 사진 변경", btnAction)

            btnAction = DialogInterface.OnClickListener { _, _ ->

            }

            photoSave.setNeutralButton("취소", btnAction)

            photoSave.show()
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