package com.example.travelusa

import android.util.Log
import java.util.LinkedList
import kotlin.random.Random

class Game {
    private lateinit var startState : String
    private lateinit var endState : String
    private lateinit var states : MutableList<String>
    private lateinit var adjacencyMatrix: Array<BooleanArray>
    private lateinit var shortestRoutes : List<List<String>>
    private var tries : Int = 10
    private var progress = 0;
    private var routeLen = 0;
    constructor(){
        createAdj()
        generateTwoStates()
    }
    private fun createAdj(){
        states = mutableListOf()
        for (str in DATASET){
            val first = str.split(" ")
            states.add(first[0])
        }
        //println(states)
        val map = DATASET.associate { entry -> val split = entry.split(" ")
        split.first() to split.drop(1)}

        //println(map)

        // Create a list of all unique vertices
        val vertices = states

        // Initialize an empty adjacency matrix
        adjacencyMatrix = Array(vertices.size) { BooleanArray(vertices.size) }

        // Populate the adjacency matrix
        for ((vertex, neighbors) in map) {
            val vertexIndex = vertices.indexOf(vertex)
            for (neighbor in neighbors) {
                val neighborIndex = vertices.indexOf(neighbor)
                adjacencyMatrix[vertexIndex][neighborIndex] = true
            }
        }

        // Print the adjacency matrix
        /*println("Adjacency Matrix:")
        for (i in vertices.indices) {
            for (j in vertices.indices) {
                print(if (adjacencyMatrix[i][j]) "1 " else "0 ")
            }
            println()
        }*/
        val (startState, endState) = generateTwoStates()
        this.startState = startState
        this.endState = endState
        shortestRoutes = findAllShortestRoutes(adjacencyMatrix, states, startState, endState)
        routeLen = shortestRoutes[0].size
        println(shortestRoutes)
        if (shortestRoutes.isNotEmpty()) {
            println("Shortest route from ${startState} to ${endState}:")
            println(shortestRoutes.joinToString(" -> "))
        } else {
            println("No route found from ${startState} to ${endState}.")
        }
    }
    fun generateTwoStates() : Pair<String, String>{
        var random = Random.Default
        var randomS = random.nextInt(0,48)
        var randomE = random.nextInt(0,48)
        while (randomS == randomE)
            randomE = random.nextInt(0,48)

        /*val contiguousStates = listOf(
            "Alabama", "Arizona", "Arkansas", "California", "Colorado", "Connecticut",
            "Delaware", "Florida", "Georgia", "Idaho", "Illinois", "Indiana", "Iowa",
            "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts",
            "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska",
            "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
            "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
            "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee",
            "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
        )*/
        val stateAbbreviations = arrayOf(
            "AL", "AZ", "AR", "CA", "CO", "CT",
            "DE", "FL", "GA", "ID", "IL", "IN", "IA",
            "KS", "KY", "LA", "ME", "MD", "MA",
            "MI", "MN", "MS", "MO", "MT", "NE",
            "NV", "NH", "NJ", "NM", "NY",
            "NC", "ND", "OH", "OK", "OR",
            "PA", "RI", "SC", "SD", "TN",
            "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
        )
        // Mapping of states to numbers from 0 to 47
        val indexToStateMap: Map<Int, String> = stateAbbreviations.withIndex().associate { it.index to it.value }
        val stateName1 = indexToStateMap[randomS] ?: "State not found"
        val stateName2 = indexToStateMap[randomE] ?: "State not found"
        return Pair(stateName1, stateName2)

    }
    fun findAllShortestRoutes(
        adjacencyMatrix: Array<BooleanArray>,
        states: List<String>,
        startState: String,
        endState: String
    ): List<List<String>> {
        val n = adjacencyMatrix.size
        val visited = BooleanArray(n)
        val queue = LinkedList<Int>()
        val parents = MutableList(n) { mutableListOf<Int>() }
        val distances = IntArray(n) { Int.MAX_VALUE }

        val startIdx = states.indexOf(startState)
        val endIdx = states.indexOf(endState)

        if (startIdx == -1 || endIdx == -1) {
            return emptyList()
        }

        queue.add(startIdx)
        distances[startIdx] = 0

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            visited[current] = true

            for (neighbor in 0 until n) {
                if (adjacencyMatrix[current][neighbor]) {
                    if (!visited[neighbor]) {
                        if (distances[current] + 1 < distances[neighbor]) {
                            distances[neighbor] = distances[current] + 1
                            parents[neighbor].clear() // Clear previous parents if new shorter distance found
                            parents[neighbor].add(current)
                            queue.add(neighbor)
                        } else if (distances[current] + 1 == distances[neighbor]) {
                            parents[neighbor].add(current) // Add parent for the same distance
                        }
                    }
                }
            }
        }

        val shortestRoutes = mutableListOf<List<String>>()

        fun buildPath(currentIdx: Int): List<List<String>> {
            if (currentIdx == startIdx) {
                return listOf(listOf(states[startIdx]))
            }

            val paths = mutableListOf<List<String>>()

            for (parentIdx in parents[currentIdx]) {
                val parentPaths = buildPath(parentIdx)
                for (path in parentPaths) {
                    val newPath = path.toMutableList()
                    newPath.add(states[currentIdx])
                    paths.add(newPath)
                }
            }

            return paths
        }

        // Modify the part where pathsFromEnd are processed
        val pathsFromEnd = buildPath(endIdx)

        for (path in pathsFromEnd) {
            shortestRoutes.add(path)
        }

        return shortestRoutes
    }
    fun getStart() : String{
        return startState
    }
    fun getEnd() : String{
        return endState
    }
    fun validate(input : String) : Boolean{

        tries--;
        Log.w("MMMMMM", shortestRoutes[0].toString())
        for (i in 0 until shortestRoutes.size) {
            // Check if the input string is present in the current inner list
            Log.w("MMMMMM", shortestRoutes[i].toString())
            if (input in shortestRoutes[i]) {
                progress++
                return true // Input string is found in one of the inner lists
            }
        }
        return false
    }
    fun getTries(): Int{
        return tries
    }
    fun gameEnd(){
        if(tries == 0){
            //go to stats page(lose)
        }else if(progress == routeLen){
            //go to stats page(win)
        }
    }
    companion object{
        val DATASET = listOf(
            "AL FL GA TN MS", "AZ NM UT NV CA", "AR LA MS TN MO OK TX",
            "CA AZ NV OR", "CO NM OK KS NE WY UT", "CT RI MA NY",
            "DE NJ PA MD", "DC MD VA", "FL GA AL", "GA SC NC TN AL FL",
            "ID WA OR NV UT WY MT", "IL WI IA MO KY IN",
            "IN IL KY OH MI", "IA MN SD NE MO IL WI", "KS OK MO NE CO",
            "KY TN VA WV OH IN IL MO", "LA MS AR TX", "ME NH", "MD DE PA WV VA DC",
            "MA NH VT NY CT RI", "MI WI IN OH KY", "MN ND SD IA WI",
            "MS AL TN AR LA", "MO AR TN KY IL IA NE KS OK", "MT ID WY SD ND",
            "NE KS MO IA SD WY CO", "NV AZ UT ID OR CA", "NH VT MA ME",
            "NJ NY PA DE", "NM TX OK CO AZ", "NY PA NJ CT MA VT",
            "NC VA TN GA SC", "ND MT SD MN", "OH MI IN KY WV PA",
            "OK TX AR MO KS CO NM", "OR CA NV ID WA", "PA OH WV MD DE NJ NY",
            "RI MA CT", "SC NC GA", "SD NE IA MN ND MT WY", "TN AL GA NC VA KY MO AR MS",
            "TX LA AR OK NM", "UT AZ CO WY ID NV", "VT NY MA NH", "VA MD DC WV KY TN NC", "WA OR ID", "WV VA MD PA OH KY",
            "WI MN IA IL MI", "WY CO NE SD MT ID UT"
        )
    }
}