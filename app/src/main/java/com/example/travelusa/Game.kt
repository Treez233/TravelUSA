package com.example.travelusa

import java.util.LinkedList

class Game {
    constructor(){
        createAdj()
    }
    private fun createAdj(){

        val states = mutableListOf<String>()
        for (str in DATASET){
            val first = str.split(" ")
            states.add(first[0])
        }
        println(states)
        val map = DATASET.associate { entry -> val split = entry.split(" ")
        split.first() to split.drop(1)}

        println(map)

        // Create a list of all unique vertices
        val vertices = states

        // Initialize an empty adjacency matrix
        val adjacencyMatrix = Array(vertices.size) { BooleanArray(vertices.size) }

        // Populate the adjacency matrix
        for ((vertex, neighbors) in map) {
            val vertexIndex = vertices.indexOf(vertex)
            for (neighbor in neighbors) {
                val neighborIndex = vertices.indexOf(neighbor)
                adjacencyMatrix[vertexIndex][neighborIndex] = true
            }
        }

        // Print the adjacency matrix
        println("Adjacency Matrix:")
        for (i in vertices.indices) {
            for (j in vertices.indices) {
                print(if (adjacencyMatrix[i][j]) "1 " else "0 ")
            }
            println()
        }
        val shortestRoutes = findAllShortestRoutes(adjacencyMatrix, states,"MD", "CA" )
        println(shortestRoutes)
        if (shortestRoutes.isNotEmpty()) {
            println("Shortest route from MD to CA:")
            println(shortestRoutes.joinToString(" -> "))
        } else {
            println("No route found from MD to CA.")
        }
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