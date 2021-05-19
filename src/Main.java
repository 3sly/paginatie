/*
 *
 *  author:  Imran Muhammad & Else Goethals
 *  date:    07/04/2021
 *  files:   Main
 *
 * */

// Imports
import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


abstract class Main {
    public static void main(String[] args) throws Exception {

        // reading xml-file and making the data usable
        File xmlFile = new File("Instructions_30_3.xml");

        // the number after the last underscore represents the amount of processes
        int amountProcesses = 3;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.parse(xmlFile);

        NodeList list = document.getElementsByTagName("instruction");

        List<Instruction> instructions = new ArrayList<>();

        for (int i = 0; i < list.getLength(); i++) {

            Node node = list.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                Instruction instruction = new Instruction (
                        Integer.parseInt(element.getElementsByTagName("processID").item(0).getTextContent()),
                        element.getElementsByTagName("operation").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("address").item(0).getTextContent())
                );
                instructions.add(instruction);
            }
        }

        // lists of our accessible data
        int amountInstructions = instructions.size();
        System.out.println(amountInstructions);

        System.out.println("Current data");
        System.out.printf("instruction -- operation -- address\n");


        for (int i = 0; i < amountInstructions; i++) {
            // params per instructions
            System.out.printf("     %-12s", instructions.get(i).getInstructionId());
            System.out.printf(" %-13s", instructions.get(i).getOperation());
            System.out.printf(" %-15s \n", instructions.get(i).getAddress());
        }

        // button 1
        firstButton.main();

        // button 2
        secondButton.main();


        // for each process (different PIDs inside the given XMLset)
        // we need to make a process and for each process a page table
        // een process bevat:
        /// a pagetable
        /// instruction(s)
        // can't be bigger than 4KByte*16pages [64KByte]
        List<Process> processes = new ArrayList<>();
        List<Process<Process.PageTable>> pageTables = new ArrayList<>();

        // create processes
        // amount of process are made and also one page table for each of them
        for(int i = 0; i < amountProcesses; i++) {

            Process process = new Process<>(
                    i
            );
            processes.add(process);

            for(int j = 0; j < 16; j++) {
                Process.PageTable pageTable  = process.new PageTable(
                        44,55,77,66
                );
               pageTables.add(pageTable);
            }
        }

        // page table has 16 [0, 15] pages for each process


        for(int i = 0; i < processes.size(); i++) {
            System.out.println("process: " + processes.get(i).pid);

            System.out.println(pageTables.size());
            for(int j  = 0; j < pageTables.size(); j++) {
                System.out.println("page entry" + j + ": "  /*pageTables.get(j).frameid + " " +  pageTables.get(j).presentBit + " " + pageTableEntries.get(j).modifyBit + " " + pageTableEntries.get(j).lastAccessTime*/);
            }
        }

        // RAM with 12 frames (2^12) aka 48kByte -> thus each frame is 4KByte
        int[] ramSpace = new int[12];

        // virtualaddressspace with 16 pages
        int[] virtualAddressSpace = new int[16];

        // for every instruction that is present we, populate our virtual address space
        // there are 4 operations: start, read, write and terminate
        // at start -> we make a new process
        // virtualaddress only used during read/write
        // at terminate we change our processes data?
        System.out.println("*********** INSTRUCTIONS ************");
        for(int i = 0; i < instructions.size(); i++) {

            // a new page gets created and the page frames also get allocated
            // it could be possible the you need to take frames that are currently taken by other processes
            if(instructions.get(i).getOperation().equals("Start")){
                System.out.println(i + "START - ");
            }
            // this only happens when read/write aka vitualspaceaddress.
            else if (instructions.get(i).getOperation().equals("Write") || instructions.get(i).getOperation().equals("Read")) {
                //System.out.println(i + " " + instructions.get(i).getAddress());
                System.out.println(i + "READ OR WRITE - ");
                // we know that there are
                // 4096 bits -> 65536 / 16 frames
                // [0, 15] -> dus afhankelijk waar het address zal vallen, nemen wij dat frame
                int address = instructions.get(i).getAddress();
                if(address >= 0 && address <= 4096) {
                    virtualAddressSpace[0] = address;
                } else  if(address >= 4097 && address <= 8192) {
                    virtualAddressSpace[1] = address;
                } else  if(address >= 8192 && address <= 12288) {
                    virtualAddressSpace[2] = address;
                } else  if(address >= 12289 && address <= 16384) {
                    virtualAddressSpace[3] = address;
                } else  if(address >= 16385 && address <= 20480) {
                    virtualAddressSpace[4] = address;
                } else  if(address >= 20481 && address <= 24576) {
                    virtualAddressSpace[5] = address;
                } else  if(address >= 24577 && address <= 28672) {
                    virtualAddressSpace[6] = address;
                } else  if(address >= 28673 && address <= 32768) {
                    virtualAddressSpace[7] = address;
                } else  if(address >= 32769 && address <= 36864) {
                    virtualAddressSpace[8] = address;
                } else  if(address >= 36865 && address <= 40960) {
                    virtualAddressSpace[9] = address;
                } else  if(address >= 40961 && address <= 45056) {
                    virtualAddressSpace[10] = address;
                } else  if(address >= 45057 && address <= 49152) {
                    virtualAddressSpace[11] = address;
                } else  if(address >= 49153 && address <= 53248) {
                    virtualAddressSpace[12] = address;
                } else  if(address >= 53249 && address <= 57344) {
                    virtualAddressSpace[13] = address;
                } else  if(address >= 57345 && address <= 61440) {
                    virtualAddressSpace[14] = address;
                } else  if(address >= 61441 && address <= 65536) {
                    virtualAddressSpace[15] = address;
                }
            }

            // page table of this process gets removed
            // and as seen above, if necessary, the frames get redistributed between the remaining processes
            else if(instructions.get(i).getOperation().equals("Terminate")) {
                System.out.println(i + "TERMINATE - ");
            }
        }


        // show the frames in RAM
        System.out.println("*********** RAM/main memory ************");
        for(int i = 0; i < ramSpace.length; i++) {
            System.out.println("FRAME " + (i+1) + ": [" + ramSpace[i] + "]");
        }

        // show the pages in virtualaddressspace
        System.out.println("*********** VIRTUAL/ secondary memory  ************");
        for(int i = 0; i < virtualAddressSpace.length; i++) {
            System.out.println("PAGE " + (i+1) + ": [" + virtualAddressSpace[i] + "]");
        }

        // this is for interface -- for later
        Userinterface userinterface = new Userinterface();
        userinterface.main(instructions, virtualAddressSpace);

    }

    // each process contains a:
    // page table
    // 1 or multiple instructions
    // kan be bigger than 4KByte*16pages [64KByte]
    public static class Process<P extends Process> {

        private int pid;


        public Process(int pid) {
            this.pid = pid;
        }

        // 16 entries per pagetable
        public class PageTable extends Process<Process> {
            private int frameid;
            private int presentBit;
            private int modifyBit;
            private int lastAccessTime;

            public PageTable(int frameid, int presentBit, int modifyBit, int lastAccessTime) {
                super(0);
                this.frameid = frameid;
                this.presentBit = presentBit;
                this.modifyBit = modifyBit;
                this.lastAccessTime = lastAccessTime;
            }
        }

    }

    // every instruction belongs to a process, look at PID if needed to know
    public static class Instruction {

        private int pid;
        private String operation;
        private int address;

        public Instruction(int instructionId, String operation, int address) {
            this.pid = pid;
            this.operation = operation;
            this.address = address;
        }

        public int getInstructionId(){
            return this.pid;
        }

        public String getOperation(){
            return this.operation;
        }

        public int getAddress(){
            return this.address;
        }
    }

    // RAM - bevat 12 frames, elke van max 4KByte ruimte voor de pages. Daarom zijn pages ook 4KByte. Normaal zit hier
    // ook de geheugensharing en page tables in bewaard.


    // aka virtuele adresruimte
}

// te doen:
// 01. address vertaling
// 02. clock incrementering
// 03. least recently used replace strategy als er nieuwe proces bijkoment ingv er geen plaats meer is
//     voo zij instructies en er plaats gemaakt moet worden.

// for 1st button
class firstButton {
    static void main() {

    }
}

// for 2nd button
class secondButton {
    static void main() {

    }
}