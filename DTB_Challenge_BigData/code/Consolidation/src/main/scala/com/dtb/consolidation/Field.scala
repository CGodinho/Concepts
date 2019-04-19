package com.dtb.consolidation

/**
  * @author CGodinho
  *
  * This object parses and stores the position of the attributes relative to a record.
  */
object Field {

    // Recursive function to support count function
    private def countRecursive(separator: Char,  quote : Char, list : List[Char], inQuotes : Boolean) : Int = {

        if (list.isEmpty)
            0
        else
            (if ((!inQuotes) && (list.head == separator)) 1 else 0) + countRecursive(separator, quote, list.tail, (inQuotes ^ (list.head == quote)))
    }

    /**
     * Calculate the number of attributes in a record, according to its tokens.
     * In case of quoted fields, separators are considered normal chars.
     *
     * @param separator - char used as separator;
     * @param quote - char used as quote;
     * @param record - record under analysis.
     * @return - # of tokens attributes.
     */
    def count(separator: Char,  quote : Char, record : String) : Int = {

        countRecursive(separator, quote, record.toList, false) + 1
    }

    // Recursive function to support tokens function
    private def tokensRecursive(separator: Char,  quote : Char, list : List[Char], inQuotes : Boolean, current : Int) : List[Int] = {

        if (list.isEmpty) return  List.empty

        if ((!inQuotes) && (list.head == separator))
            current :: tokensRecursive(separator, quote, list.tail, (inQuotes ^ (list.head == quote)), current + 1)
        else
            tokensRecursive(separator, quote, list.tail, (inQuotes ^ (list.head == quote)), current + 1)
    }

  /**
    * Calculate the tokens list, which represents the positions of the separator quotes.
    * In case of quoted fields, separators are considered normal chars.
    * For further processing simplification, position -1 and length are considered as token positions.
    *
    * @param separator - char used as separator;
    * @param quote - char used as quote;
    * @param record - record under analysis.
    * @return - tokens list.
    */
    def tokens(separator: Char,  quote : Char, record : String) : List[Int] = {

        -1 :: (tokensRecursive(separator, '"', record.toList, false, 0) :+ (record.length))
    }

    /**
     * Gets a parameter from a record, with the help of the parsed token list.
     * If the parameter is quoted, quotes are removed and any ',' substituted by ';'.
     * Value is trimmed before returned.
     *
     * @param position - parameter position;
     * @param record  - record under processing;
     * @param distance - tokens list.
     * @return - parameter as a string
     */
    def getToken(position : Int, record : String, tokens: List[Int]) : String = {

        val token = record.substring(tokens(position - 1) + 1, tokens(position)).trim

        if (Constants.RegExQuoted.pattern.matcher(token).matches)
            token.substring(1, token.length - 1).replace(',', ';').trim
        else
            token
     }
}