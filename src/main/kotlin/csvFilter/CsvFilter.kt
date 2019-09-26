package csvFilter

class CsvFilter {
    fun filter(lines: List<String>): List<String> {
        val result  = mutableListOf<String>()
        result.add(lines[0])
        val header = result.get(0)
        val invoice = lines[1]
        val fields = invoice.split(',')
        val grossFieldIndex = 2
        val netFieldIndex = 3
        val ivaFieldIndex = 4
        val igicFieldIndex = 5
        val cifFieldIndex = 7
        val nifFieldIndex = 8
        val ivaField = fields[ivaFieldIndex]
        val igicField = fields[igicFieldIndex]
        val grossField = fields[grossFieldIndex]
        val netField = fields[netFieldIndex]
        val cifField = fields[cifFieldIndex]
        val nifField = fields[nifFieldIndex]
        val decimalRegex = "\\d+(\\. \\d+)?".toRegex()
        val taxFieldsAreMutuallyExclusive =
            (ivaField.matches(decimalRegex)|| igicField.matches(decimalRegex)) &&
            (ivaField.isNullOrEmpty() || igicField.isNullOrEmpty())

        val taxIdentificationFieldsAreMutuallyExclusive =
                (cifField.isNullOrEmpty() || nifField.isNullOrEmpty()) &&
                (!(cifField.isNullOrEmpty() && nifField.isNullOrEmpty()))

        var net = 0.toBigDecimal()
        if(taxFieldsAreMutuallyExclusive               &&
          !header.isNullOrEmpty()                      &&
           taxIdentificationFieldsAreMutuallyExclusive &&
          (cifField.length == 9 || nifField.length == 9)){
            if (!ivaField.isNullOrEmpty()) net = grossField.toBigDecimal() - ((grossField.toBigDecimal() * ivaField.toBigDecimal())/100.toBigDecimal())
            if (!igicField.isNullOrEmpty()) net = grossField.toBigDecimal() - ((grossField.toBigDecimal() * igicField.toBigDecimal())/100.toBigDecimal())
            if(net == netField.toBigDecimal()) result.add(lines[1])
        }

        return result.toList()
    }

}