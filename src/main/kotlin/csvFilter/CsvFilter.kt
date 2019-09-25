package csvFilter

import java.math.BigDecimal

class CsvFilter {
    fun filter(lines: List<String>): List<String> {
        val result  = mutableListOf<String>()
        result.add(lines[0])
        val invoice = lines[1]
        val fields = invoice.split(',')
        val header = result.get(0)
        val grossFieldIndex = 2
        val netFieldIndex = 3
        val ivaFieldIndex = 4
        val igicFieldIndex = 5
        val ivaField = fields[ivaFieldIndex]
        val igicField = fields[igicFieldIndex]
        val grossField = fields[grossFieldIndex]
        val netField = fields[netFieldIndex]
        val decimalRegex = "\\d+(\\. \\d+)?".toRegex()
        val taxFieldsAreMutuallyExclusive =
            (ivaField.matches(decimalRegex)|| igicField.matches(decimalRegex)) &&
            (ivaField.isNullOrEmpty() || igicField.isNullOrEmpty())

        var net = 0.toBigDecimal()
        if(taxFieldsAreMutuallyExclusive && header != ""){
            if (!ivaField.isNullOrEmpty()) net = grossField.toBigDecimal() - ((grossField.toBigDecimal() * ivaField.toBigDecimal())/100.toBigDecimal())
            if (!igicField.isNullOrEmpty()) net = grossField.toBigDecimal() - ((grossField.toBigDecimal() * igicField.toBigDecimal())/100.toBigDecimal())
            if(net == netField.toBigDecimal()) result.add(lines[1])
        }

        return result.toList()
    }

}