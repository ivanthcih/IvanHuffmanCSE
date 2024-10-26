import java.util.*;
import java.io.*;

/*
 * Ivan Thich
 * CSE 123
 * P3: Huffman
 * TA: Zachary Bi
 * 05/31/2024
 * HuffmanCode.java
 * A program that follows Huffman encoding algorithm which 
 * compresses information from words, into binary representation.
 */

 public class HuffmanCode{
 
    private static final char ZERO = '0';
    private static final char ONE = '1';
    private static final char NULL_CHAR = '\0';
    private HuffmanNode front;

    /*
     * Initializes a new Huffman Tree with an array.
     * @param
     *  frequencies -> An array of frequencies where 
     *                  it holds the count of each character
     */
    public HuffmanCode(int[] frequencies){
        Queue<HuffmanNode> q = new PriorityQueue<>();
        for(int i = 0; i < frequencies.length; i++){
            if(frequencies[i] > 0){
                q.add(new HuffmanNode((char) i, frequencies[i]));
            }
        }
        // More than one thing inside the q
        while(q.size() > 1){
            // Take out the first two HuffmanNodes
            HuffmanNode one = q.remove();
            HuffmanNode two = q.remove();
            // HuffmanNode.freq + HuffmanNode2.freq
            int totalFreq = one.freq + two.freq;
            // Create new huffmanNode w/ combined freq and some value
            HuffmanNode combo = new HuffmanNode('\0', totalFreq, one, two);
            q.add(combo);
        }
        front = q.remove();
    }

    /*
     * Stores the current HuffmanCode in .code file.
     * @param
     *  output -> The file it is going to be stored in
     */
    public void save(PrintStream output){
        save(output, front, "");
    }

    /*
     * Assists in storing the current 
     * HuffmanCode in .code file.
     * @param
     *  output -> The file it is going to be stored in
     *  root -> Current root that references front
     */
    private void save(PrintStream output, HuffmanNode root, String soFar){
        if (root != null) {
            if(root.left == null && root.right == null && root.data != NULL_CHAR){
                output.println((int) root.data);
                output.println(soFar);
            }else{
                save(output, root.left, soFar + "0");
                save(output, root.right, soFar + "1");
            }
        }
    }

    /*
     * Initializes a new Huffman Tree by reading previously
     * constructed code from a .code file.
     * @param
     *  input -> The .code file
     */
    public HuffmanCode(Scanner input){
        while(input.hasNextLine()){
            int asciiVal = Integer.parseInt(input.nextLine());
            String code = input.nextLine();
            front = scanHelper(asciiVal, code, front);
        }
    }

    /*
     * Assists with Scanner constructor 
     * in constructing A Huffman Tree.
     * @param
     *  asciiVal -> The char value that is put in the node
     *  code -> The position of the node in the tree
     *  root -> Current root that references front 
     */
    private HuffmanNode scanHelper(int asciiVal, String code, HuffmanNode root){
        // If the code length is at one or need to create a null node
        if(code.length() == 0){
            return new HuffmanNode((char) asciiVal, 0);
        }
        else if(root == null){
            root = new HuffmanNode(NULL_CHAR, 0);
        }
        // If the code length is greater or equal to one
        char holdChar = code.charAt(0);
        if(holdChar == ZERO){
            root.left = scanHelper(asciiVal, code.substring(1), root.left);
        }
        else if(holdChar == ONE){
            root.right = scanHelper(asciiVal, code.substring(1), root.right);
        }
        return root;
    }

    /*
     * Reads individual bits from the input 
     * and prints it out the corresponding letters in the output.
     * @param
     *  input -> Whole binary representation of text
     *  output -> The file it is going to be stored in
     */
    public void translate(BitInputStream input, PrintStream output){
        translate(input, output, front);
    }

    /*
     * Assist in translating from binary representation to word.
     * @param
     *  input -> Whole binary representation of text
     *  output -> The file it is going to be stored in
     *  root -> Current root that references front 
     */
    private void translate(BitInputStream input, PrintStream output, HuffmanNode root){
        while(input.hasNextBit()){
            int numVal = input.nextBit();
            if(numVal == 0){
                root = root.left;
            }else if(numVal == 1){
                root = root.right;
            }
            // When we arrive at the letter, or 'end' of the tree
            if(root.left == null || root.right == null){
                output.write(root.data);
                root = front;
            }
        }

    }

    /*
     * Represent nodes of HuffmanCode
     */
    public static class HuffmanNode implements Comparable<HuffmanNode> {
        // All fields for HuffmanNode
        public final char data;
        public final int freq;
        public HuffmanNode left;
        public HuffmanNode right;

        /*
         * Constructor for a singular node
         * @param
         *  data -> The char
         *  freq -> The frequency the char appears
         */
        public HuffmanNode(char data, int freq){
            this(data, freq, null, null);
        }

        /*
         * Constructor for a singular node w/ left and right nodes attached
         * @param
         *  data -> The char
         *  freq -> The frequency the char appears
         *  left -> A node that is attached to the left
         *  right -> A node that is attached to the right
         */
        public HuffmanNode(char data, int freq, HuffmanNode left, HuffmanNode right){
            this.data = data;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        /*
         * Compares two individual nodes based on frequency
         * @param
         *  other -> Another node that is compared to the current
         * @return
         *  1 -> Current node has a greater freq than other node
         * -1 -> Current node has a lower freq than other node
         *  0 -> Both nodes are equal
         */
        @Override
        public int compareTo(HuffmanNode other) {
            if(this.freq > other.freq){
                return 1;
            }else if(this.freq < other.freq){
                return -1;
            }
            return 0;
        }
    }
}
