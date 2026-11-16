package com.example.goldenflowerai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

data class RoundRecord(val a: Long, val b: Long, val c: Long, val winner: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { TrackerScreen() } }
    }
}

@Composable
fun TrackerScreen() {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    var c by remember { mutableStateOf("") }
    var winner by remember { mutableStateOf("A") }
    val records = remember { mutableStateListOf<RoundRecord>() }
    val counts = listOf("A", "B", "C").associateWith { key ->
        records.count { it.winner == key }
    }
    val total = records.size
    val rates = listOf("A", "B", "C").associateWith { key ->
        if (total == 0) 0.0 else counts.getValue(key) * 100.0 / total
    }
    val leading = rates.maxByOrNull { it.value }
    val enoughData = total >= 30

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Golden Flower AI Signal", style = MaterialTheme.typography.headlineSmall)
                Text("রাউন্ডের তথ্য লিখে ইতিহাস ও পরিসংখ্যান দেখুন। এটি জয়ের পূর্বাভাস নয়।")
            }
            item { NumberField("A Pot", a) { a = it } }
            item { NumberField("B Pot", b) { b = it } }
            item { NumberField("C Pot", c) { c = it } }
            item {
                Text("রাউন্ডের বিজয়ী (ফলাফল জানার পরে)")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("A", "B", "C").forEach { option ->
                        FilterChip(
                            selected = winner == option,
                            onClick = { winner = option },
                            label = { Text(option) }
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val av = a.toLongOrNull()
                        val bv = b.toLongOrNull()
                        val cv = c.toLongOrNull()
                        if (av != null && bv != null && cv != null &&
                            av >= 0 && bv >= 0 && cv >= 0
                        ) {
                            records.add(0, RoundRecord(av, bv, cv, winner))
                            a = ""; b = ""; c = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("রাউন্ড সংরক্ষণ করুন") }
            }
            item {
                HorizontalDivider()
                Text("ইতিহাসভিত্তিক সারাংশ", style = MaterialTheme.typography.titleLarge)
                Text("মোট সংরক্ষিত রাউন্ড: $total")
                listOf("A", "B", "C").forEach { key ->
                    Text("$key: ${counts.getValue(key)} বার • ${String.format(Locale.US, "%.1f", rates.getValue(key))}%")
                }
                if (enoughData && leading != null) {
                    Text("ইতিহাসে সর্বোচ্চ হার: ${leading.key} (${String.format(Locale.US, "%.1f", leading.value)}%)")
                    Text("এটি শুধু অতীতের হার; পরের রাউন্ডের সম্ভাবনা বা নিশ্চিত সিগন্যাল নয়।")
                } else {
                    Text("ঐতিহাসিক হার দেখাতে অন্তত ৩০টি ফলাফল সংরক্ষণ করুন।")
                }
            }
            item {
                Text("সাম্প্রতিক রাউন্ড", style = MaterialTheme.typography.titleLarge)
                Text("নতুন রেকর্ড উপরে দেখানো হবে।")
            }
            items(records.take(50)) { r ->
                Card(Modifier.fillMaxWidth()) {
                    Text("বিজয়ী ${r.winner}  |  Pot: A ${r.a}, B ${r.b}, C ${r.c}",
                        modifier = Modifier.padding(12.dp))
                }
            }
            item {
                OutlinedButton(
                    onClick = { records.clear() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("সব ইতিহাস মুছুন") }
                Text("সতর্কতা: এই প্রাথমিক সংস্করণে তথ্য ফোনে স্থায়ীভাবে সংরক্ষিত নয়। অ্যাপ বন্ধ করলে রেকর্ড মুছে যাবে।")
            }
        }
    }
}

@Composable
fun NumberField(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> if (input.all(Char::isDigit)) onValue(input) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
