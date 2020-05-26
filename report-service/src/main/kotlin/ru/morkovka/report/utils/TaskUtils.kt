package ru.morkovka.report.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

class TaskUtils {
    companion object {

        /**
         * Sorts all items in map by project name then by task number:
         * "DM-1" < "DM-2" < "DM-3" < "NEW-1" < "NEW-2"
         *
         * @param map to sort. A key in map is a task jiraKey.
         * @return sorted map
         */
        fun sortByJiraKey(map: MutableMap<String, MutableList<String>>) =
            map.toSortedMap(compareBy<String> { getProjectNameFromJiraKey(it) }.thenBy { getNumberFromJiraKey(it) })

        fun getMatcher(str: String): Matcher {
            val pattern =
                Pattern.compile("(.*)-(\\d+)", Pattern.CASE_INSENSITIVE)
            return pattern.matcher(str)
        }

        fun getNumberFromJiraKey(str: String): Int {
            val matcher = getMatcher(str)
            matcher.find()
            return matcher.group(2).toInt()
        }

        fun getProjectNameFromJiraKey(str: String): String {
            val matcher = getMatcher(str)
            matcher.find()
            return matcher.group(1).toString()
        }
    }
}