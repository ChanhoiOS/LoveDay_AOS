package com.chanho.loveday

import android.os.Build
import android.os.Bundle
import android.view.*
import com.chanho.loveday.databinding.FragmentMemoBinding
import com.chanho.loveday.databinding.MemoWriteViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MemoWriteFragment : BottomSheetDialogFragment() {

    private lateinit var binding: MemoWriteViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MemoWriteViewBinding.inflate(inflater, container, false )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다이얼로그 내부의 뷰들을 초기화하거나 작업을 수행할 수 있습니다.

        // 키보드 올라갈 때 함께 올라가도록 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog?.window?.setDecorFitsSystemWindows(false)
            binding.root.setOnApplyWindowInsetsListener { _, insets ->
                val topInset = insets.getInsets(WindowInsets.Type.statusBars()).top
                val imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom
                val navigationHeight = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
                val bottomInset = if (imeHeight == 0) navigationHeight else imeHeight
                binding.root.setPadding(0, topInset, 0, bottomInset)
                insets
            }
        } else {
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }
}
