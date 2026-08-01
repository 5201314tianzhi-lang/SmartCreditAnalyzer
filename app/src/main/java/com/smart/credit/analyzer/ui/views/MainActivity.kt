package com.smart.credit.analyzer.ui.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.smart.credit.analyzer.R
import com.smart.credit.analyzer.databinding.ActivityMainBinding
import com.smart.credit.analyzer.presentation.CreditReportViewModel
import com.smart.credit.analyzer.repository.CreditReportRepository
import com.smart.credit.analyzer.util.pdf.CreditReportPdfParser
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 主界面 Activity
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CreditReportViewModel
    
    // PDF 文件选择器
    private val pdfPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handlePdfFile(it) }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化 ViewModel
        val repository = CreditReportRepository(applicationContext)
        viewModel = ViewModelProvider(this, 
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CreditReportViewModel::class.java]
        
        setupUI()
        observeState()
    }
    
    private fun setupUI() {
        // 新建报告按钮
        binding.btnNewReport.setOnClickListener {
            openPdfPicker()
        }
        
        // 扫描按钮（摄像头，后续实现）
        binding.btnScanPdf.setOnClickListener {
            Toast.makeText(this, "OCR扫描功能即将推出", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when {
                    state.isLoading -> showLoading(true)
                    state.selectedReport != null -> showReportDetail(state.selectedReport)
                    else -> showLoading(false)
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { error ->
                error?.let { showToast(it) }
            }
        }
    }
    
    private fun openPdfPicker() {
        pdfPicker.launch("application/pdf")
    }
    
    private fun handlePdfFile(uri: Uri) {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("无法打开文件")
                
                val parser = CreditReportPdfParser()
                val report = parser.parse(inputStream)
                
                // 保存到数据库并分析
                val reportId = viewModel.saveReport(report)
                
                showLoading(false)
                showToast("解析成功，ID: $reportId")
                
            } catch (e: Exception) {
                showLoading(false)
                showToast("解析失败: ${e.message}")
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun showReportDetail(uiModel: Any) {
        // TODO: 导航到详情页面
    }
}