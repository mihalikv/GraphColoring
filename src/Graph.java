// A Java program to implement greedy algorithm for graph coloring
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

class ValueComparator implements Comparator<Integer> {
    Map<Integer, Integer> base;

    public ValueComparator(Map<Integer, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

// This class represents an undirected graph using adjacency list
class Graph
{
    private int V;   // No. of vertices
    private BufferedReader br;
    private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    private TreeMap<Integer, Integer> sorted_map;
    private RandomAccessFile raf;
    private int init_seek;
    private int one_line;
    private boolean welsh;

    //Constructor
    Graph(String fileName, boolean welsh)
    {
        this.welsh = welsh;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            String line = br.readLine();
            init_seek = line.length() + 2;
            V = Integer.parseInt(line);
            one_line = V * 2 + 1;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if(welsh) {
            this.prepare_order();
            try {
                raf = new RandomAccessFile(fileName, "r");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void prepare_order()
    {
        String line;
        try {
            int index = 0;
            while((line = br.readLine()) != null){
                int count = line.length() - line.replace("1", "").length();
                map.put(index, count);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ValueComparator bvc = new ValueComparator(map);
        this.sorted_map = new TreeMap<Integer, Integer>(bvc);
        this.sorted_map.putAll(map);
    }

    // Assigns colors (starting from 0) to all vertices and
    // prints the assignment of colors
    void greedyColoring(){
        int result[] = new int[V];

        // Initialize all vertices as unassigned
        Arrays.fill(result, -1);

        // Assign the first color to first vertex
        int first_value = 0;
        if (welsh) {
            first_value = this.sorted_map.firstKey();
            result[first_value] = 0;
        }else{
            result[0] = 0;
            try {
                br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // A temporary array to store the available colors. False
        // value of available[cr] would mean that the color cr is
        // assigned to one of its adjacent vertices
        boolean available[] = new boolean[V];

        // Initially, all colors are available
        Arrays.fill(available, true);
        String line;
        // Assign colors to remaining V-1 vertices
        if (welsh){
            for(Map.Entry<Integer,Integer> entry : this.sorted_map.entrySet()) {
                Integer key = entry.getKey();
                if (key == first_value)
                    continue;
                try {
                    raf.seek(init_seek + one_line * key);
                    line = raf.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                String[] edges = line.split(" ");
                for(int i = 0; i < edges.length; i++)
                {
                    if (Integer.parseInt(edges[i]) == 1 && result[i] != -1)
                        available[result[i]] = false;
                }

                // Find the first available color
                int cr;
                for (cr = 0; cr < V; cr++){
                    if (available[cr])
                        break;
                }

                result[key] = cr; // Assign the found color

                // Reset the values back to true for the next iteration
                Arrays.fill(available, true);
            }
        }else{
            for (int u = 1; u < V; u++)
            {
                // Process all adjacent vertices and flag their colors
                // as unavailable
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                String[] edges = line.split(" ");
                for(int i = 0; i < edges.length; i++)
                {
                    if (Integer.parseInt(edges[i]) == 1 && result[i] != -1)
                        available[result[i]] = false;
                }

                // Find the first available color
                int cr;
                for (cr = 0; cr < V; cr++){
                    if (available[cr])
                        break;
                }

                result[u] = cr; // Assign the found color

                // Reset the values back to true for the next iteration
                Arrays.fill(available, true);
            }
        }




//        // print the result
//        for (int u = 0; u < V; u++)
//            System.out.println("Vertex " + u + " --->  Color " + result[u]);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("out.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < result.length; i++) {
            writer.println(result[i]);
        }
        writer.close();
        System.out.println("Chromantic number: " + (Arrays.stream(result).max().getAsInt() + 1));
    }


    // Driver method
    public static void main(String args[])
    {
        long startTime = System.nanoTime();

        Graph g1;
        g1 = new Graph("grafy/matrix5000.txt", false);
        g1.greedyColoring();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        System.out.println("Execution time with sort: " + duration + "ns" + ", " + duration/1000000 + "ms");
    }
}