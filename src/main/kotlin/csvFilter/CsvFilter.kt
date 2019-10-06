package csvFilter

class CsvFilter {
    private val invoicesIds : MutableMap<String, Int> = mutableMapOf()
    private val percentage = 1/100.toDouble()

    fun apply(lines: List<String>): List<String> {
        val result  = mutableListOf<String>()
        val header = lines.get(0)
        if(header.isNullOrEmpty()) return listOf("")

        result.add(header)
        var invoices = lines.subList(1, lines.size)

        for(invoice in invoices){
            val invoiceId = invoice.get(0)
            if (invoicesIds.containsKey(invoiceId.toString())){
                var value = invoicesIds.get(invoiceId.toString())
                invoicesIds.put(invoiceId.toString(), value!!.plus(1))
            } else {
                invoicesIds.put(invoiceId.toString(), 1)
            }
        }

        invoices.forEach {invoice ->
            if(isValid(invoice)){
                result.add(invoice)
            }
        }

        return result.toList()
    }

    fun isValid(invoice : String) :Boolean{
        val fields = invoice.split(',')
        val invoiceId = invoice.get(0)
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

        if (invoice.isNullOrEmpty() || invoicesIds.get(invoiceId.toString())!! > 1) return false

        val decimalRegex = "\\d+(\\. \\d+)?".toRegex()
        val taxFieldsAreMutuallyExclusive =
            (ivaField.matches(decimalRegex) || igicField.matches(decimalRegex)) &&
                    (ivaField.isNullOrEmpty() || igicField.isNullOrEmpty())

        val taxIdentificationFieldsAreMutuallyExclusive =
            (cifField.isNullOrEmpty() || nifField.isNullOrEmpty()) &&
                    (!(cifField.isNullOrEmpty() && nifField.isNullOrEmpty()))

        if (taxFieldsAreMutuallyExclusive &&
            taxIdentificationFieldsAreMutuallyExclusive &&
            (cifField.matches("^[A-Za-z]\\d{7}([A-Za-z]|\\d)".toRegex()) || nifField.matches("\\d{8}[A-Za-z]".toRegex()))) {

            var tax = if (ivaField.isNullOrEmpty()) igicField else ivaField
            var net = grossField.toDouble() - grossField.toDouble() * tax.toDouble() * percentage
            if (net == netField.toDouble()) return true
        }
        return false
    }
}