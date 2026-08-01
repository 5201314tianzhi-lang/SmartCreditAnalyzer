package com.smart.credit.analyzer.ui.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.smart.credit.analyzer.R
import com.smart.credit.analyzer.databinding.ActivityNewReportBinding
import com.smart.credit.analyzer.util.pdf.CreditReportPdfParser
import kotlinx.coroutines.launch

/**
 * 新建报告 Activity - 选择PDF文件
 */
class NewReportActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNewReportBinding
    
    private val pdfPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { analyzePdf(it) }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
        
        binding.btnChooseFile.setOnClickListener {
            openPdfPicker()
        }
    }
    
    private fun openPdfPicker() {
        pdfPicker.launch("application/pdf")
    }
    
    private fun analyzePdf(uri: Uri) {
        binding.tvStatus.text = getString(R.string.status_parsing_pdf)
        binding.progressBar.visibility = android.view.View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("无法打开文件")
                
                val parser = CreditReportPdfParser()
                val report = parser.parse(inputStream)
                
                // 保存结果到 Intent
                val resultIntent = Intent().apply {
                    putExtra("REPORT_DATA", report)
                    putExtra("FILE_URI", uri.toString())
                }
                
                setResult(RESULT_OK, resultIntent)
                finish()
                
            } catch (e: Exception) {
                binding.tvStatus.text = getString(R.string.error_parsing_failed)
                Toast.makeText(this@NewReportActivity, 
                    "解析失败: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }
}