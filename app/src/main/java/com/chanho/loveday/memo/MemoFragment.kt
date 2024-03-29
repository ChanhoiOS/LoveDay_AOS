package com.chanho.loveday.memo

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chanho.loveday.MemoDataListener
import com.chanho.loveday.MemoWriteFragment
import com.chanho.loveday.NetworkManager
import com.chanho.loveday.R
import com.chanho.loveday.adapter.GridSpacingItemDecoration
import com.chanho.loveday.adapter.MemoItemAdapter
import com.chanho.loveday.application.MyApplication
import com.chanho.loveday.databinding.FragmentMemoBinding
import com.chanho.loveday.dday.DDayViewModel
import com.chanho.loveday.model.MemoModel
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MemoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MemoFragment : Fragment(), MemoDataListener {
    private lateinit var binding: FragmentMemoBinding
    private lateinit var viewModel: MemoViewModel
    lateinit var adapter: MemoItemAdapter

    private val spanCount = 2 // 열의 개수
    private val spacing = 30 // 아이템 간격 (dp 단위)
    private val includeEdge = true // 가장자리에도 간격을 포함할지 여부

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoBinding.inflate(inflater, container, false )
        viewModel = ViewModelProvider(requireActivity()).get(MemoViewModel::class.java)

        setObserver()
        setRecyclerView()
        setData()
        swipeRefresh()
        setBtnEvent()

        return binding.root
    }

    private fun setBtnEvent() {
        binding.memoKeyButton.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("사랑의 키 등록")
            builder.setMessage("설정창에 있는 상대의 키를 등록하고 메모를 공유해요!")

            val input = EditText(requireContext())
            input.hint = "LoveDay"
            builder.setView(input)

            builder.setPositiveButton("등록") { dialog, _ ->
                val enteredText = input.text.toString()
                if (enteredText.isNotBlank()) {
                    MyApplication.prefs.setString("partnerKey", enteredText)

                    registerKey(enteredText)
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }

        binding.memoRegisterButton.setOnClickListener {
            val memoTakeDialog = MemoWriteFragment()
            memoTakeDialog.setMemoDataListener(this, false)
            memoTakeDialog.show(requireActivity().supportFragmentManager, "memo_take_dialog")
        }
    }

    private fun swipeRefresh() {
        binding.swipeLayout.setOnRefreshListener {
            setData()
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun setRecyclerView() {
        val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, includeEdge)
        binding.memoRecyclerView.addItemDecoration(itemDecoration)
    }

    private fun setData() {
        val privateKey = MyApplication.prefs.getString("privateKey", "")
        val partnerKey = MyApplication.prefs.getString("partnerKey", "")

        val param = HashMap<String, Any>()
        param["writer"] = privateKey
        if (partnerKey != "") {
            param["partner"] = partnerKey
            binding.memoKeyButton.setImageResource(R.drawable.main_heart_middle)
        }
        fetchData(param)
    }

    private fun fetchData(param: HashMap<String, Any>) {
        viewModel.fetchData(param)
    }

    private fun setObserver() {
        viewModel.memoModelData.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter = MemoItemAdapter(this, it)
                binding.memoRecyclerView.adapter = adapter
            }
        })

        viewModel.memoRegisterSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    setData()
                    memoSendNoti("등록")
                    viewModel.memoRegisterSuccess.value = false
                } else {

                }
            }
        })

        viewModel.memoUpdateSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    setData()
                    memoSendNoti("수정")
                    viewModel.memoUpdateSuccess.value = false
                } else {

                }
            }
        })

        viewModel.memoDeleteSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    setData()
                    viewModel.memoDeleteSuccess.value = false
                } else {

                }
            }
        })

        viewModel.keyRegister.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    setData()
                    binding.memoKeyButton.setImageResource(R.drawable.main_heart_middle)
                } else {

                }
            }
        })

        viewModel.sendNotification.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Log.e("노티 발송 성공", it.toString())
                } else {
                    Log.e("노티 발송 실패", it.toString())
                }
            }
        })
    }

    override fun onMemoDataEntered(isEdit: Boolean, id: Int, title: String, content: String) {
        val privateKey = MyApplication.prefs.getString("privateKey", "")

        val param = HashMap<String, Any>()

        if (isEdit) {
            param["id"] = id
            param["title"] = title
            param["content"] = content
            updateMemo(param)
        } else {
            param["writer"] = privateKey
            param["title"] = title
            param["content"] = content
            registerMemo(param)
        }
    }

    private fun registerMemo(data: HashMap<String, Any>) {
        viewModel.registerMemo(data)
    }

    private fun updateMemo(data: HashMap<String, Any>) {
        viewModel.updateMemo(data)
    }

    private fun deleteMemo(data: HashMap<String, Any>) {
        viewModel.deleteMemo(data)
    }

    fun moreAction(data: Map<String, Any>) {
        val privateKey = MyApplication.prefs.getString("privateKey", "")

        val momoDialog = AlertDialog.Builder(requireContext())
        momoDialog.setTitle("메모 관리")
        momoDialog.setMessage("(상대방에 의해 작성된 메모는 수정/삭제할 수 없습니다.)")

        var btnAction: DialogInterface.OnClickListener?

        val id = data["id"] as? Int ?: 0
        val writer = data["writer"] as? String ?: ""
        val title = data["title"] as? String ?: ""
        val content = data["content"] as? String ?: ""

        btnAction = DialogInterface.OnClickListener { _, _ ->
            if (writer == privateKey) {
                val memoTakeDialog = MemoWriteFragment()

                val param = HashMap<String, Any>()
                param["writer"] = writer
                param["id"] = id
                param["title"] = title
                param["content"] = content

                memoTakeDialog.setMemoDataListener(this, true, id)
                memoTakeDialog.show(requireActivity().supportFragmentManager, "memo_take_dialog")
            } else {
                Toast.makeText(requireContext(), "상대방에 의해 작성된 메모는 수정할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        momoDialog.setPositiveButton("수정", btnAction)

        btnAction = DialogInterface.OnClickListener { _, _ ->
            val param = HashMap<String, Any>()

            if (writer == privateKey) {
                param["writer"] = writer
                param["id"] = id
                deleteMemo(param)
            } else {
                Toast.makeText(requireContext(), "상대방에 의해 작성된 메모는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        momoDialog.setNegativeButton("삭제", btnAction)

        btnAction = DialogInterface.OnClickListener { _, _ ->
            println("취소")
        }
        momoDialog.setNeutralButton("취소", btnAction)

        momoDialog.show()
    }

    fun registerKey(partner: String) {
        val privateKey = MyApplication.prefs.getString("privateKey", "")
        val token = MyApplication.prefs.getString("fcmToken", "")

        val param = HashMap<String, Any>()
        param["partner"] = partner
        param["writer"] = privateKey
        param["token"] = token

        viewModel.registerKey(param)
    }

    private fun memoSendNoti(type: String) {
        val partnerKey = MyApplication.prefs.getString("partnerKey", "")
        val param = HashMap<String, Any>()

        param["partner"] = partnerKey
        param["type"] = type

        viewModel.memoSendNoti(param)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MemoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MemoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}