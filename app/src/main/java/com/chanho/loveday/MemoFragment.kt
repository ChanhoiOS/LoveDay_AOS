package com.chanho.loveday

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chanho.loveday.adapter.MemoItemAdapter
import com.chanho.loveday.databinding.FragmentMemoBinding
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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMemoBinding
    private var memoModelData: List<MemoModel>? = null
    lateinit var adapter: MemoItemAdapter

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
        binding = FragmentMemoBinding.inflate(inflater, container, false )

        setData()
        swipeRefresh()

        binding.memoRegisterButton.setOnClickListener {
            val memoTakeDialog = MemoWriteFragment()
            memoTakeDialog.setMemoDataListener(this, false)
            memoTakeDialog.show(requireActivity().supportFragmentManager, "memo_take_dialog")
        }

        return binding.root
    }

    private fun swipeRefresh() {
        binding.swipeLayout.setOnRefreshListener {
            setData()
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun setData() {
        val param = HashMap<String, Any>()
        param["writer"] = "sI3fpy"
        fetchData(param)
    }

    private fun fetchData(param: HashMap<String, Any>) {
        NetworkManager.getMemo(param) { data ->
            if (data != null) {
                var sortedData = data.sortedByDescending { it.id ?: Int.MIN_VALUE }
                memoModelData = sortedData
                memoModelData?.let {
                    adapter = MemoItemAdapter(this, memoModelData)
                    binding.memoRecyclerView.adapter = adapter
                }
            } else {
                // 데이터 가져오기 실패
            }
        }
    }

    override fun onMemoDataEntered(isEdit: Boolean, id: Int, title: String, content: String) {
        val param = HashMap<String, Any>()

        if (isEdit) {
            param["id"] = id
            param["title"] = title
            param["content"] = content
            updateMemo(param)
        } else {
            param["writer"] = "sI3fpy"
            param["title"] = title
            param["content"] = content
            registerMemo(param)
        }
    }

    private fun registerMemo(data: HashMap<String, Any>) {
        NetworkManager.postMemoRequest(data, {
            setData()
        }, {

        })
    }

    private fun deleteMemo(data: HashMap<String, Any>) {
        NetworkManager.deleteMemo(data, {
            setData()
        }, {

        })
    }

    private fun updateMemo(data: HashMap<String, Any>) {
        NetworkManager.updateMemo(data, {
            setData()
        }, {

        })
    }

    fun moreAction(data: Map<String, Any>) {

        val momoDialog = AlertDialog.Builder(requireContext())
        var btnAction: DialogInterface.OnClickListener?

        val id = data["id"] as? Int ?: 0
        val writer = data["writer"] as? String ?: ""
        val title = data["title"] as? String ?: ""
        val content = data["content"] as? String ?: ""

        btnAction = DialogInterface.OnClickListener { _, _ ->
            val memoTakeDialog = MemoWriteFragment()

            val param = HashMap<String, Any>()
            param["writer"] = writer
            param["id"] = id
            param["title"] = title
            param["content"] = content

            memoTakeDialog.setMemoDataListener(this, true, id)
            memoTakeDialog.show(requireActivity().supportFragmentManager, "memo_take_dialog")
        }
        momoDialog.setPositiveButton("수정", btnAction)

        btnAction = DialogInterface.OnClickListener { _, _ ->
            val param = HashMap<String, Any>()
            param["writer"] = writer
            param["id"] = id
            deleteMemo(param)
        }
        momoDialog.setNegativeButton("삭제", btnAction)

        btnAction = DialogInterface.OnClickListener { _, _ ->
            println("취소")
        }
        momoDialog.setNeutralButton("취소", btnAction)

        momoDialog.show()
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