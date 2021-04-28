/*
 *
 *  author:  Imran Muhammad & Else Goethals
 *  date:    07/04/2021
 *  files:   Main
 *
 * */

// Imports
import java.io.File;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
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
        List<PageTable> pageTables = new ArrayList<>();
        // lists of our accessible data
        int amountInstructions = instructions.size();
        System.out.println(amountInstructions);
        System.out.println("Current data");
        System.out.printf("instruction -- operation -- address\n");
        int timer = 0;

        //Methodes hieronder moeten geactiveerd worden afhankelijk van de knop in de interface die ingedrukt wordt.
        //Laat triggerOneInstruction een stringbuilder terugsturen ofzo met al de gegevens die je nodig hebt en die na het uitvoeren van de functie in je interface zetten.
        triggerOneInstruction(instructions.get(timer), pageTables);
        timer ++;
        triggerAllInstruction(instructions, pageTables, timer);
        Userinterface userinterface = new Userinterface();

        userinterface.main(null);
    }

    public static void triggerOneInstruction(Instruction currentInstruction, List<PageTable> pageTables){
        // params per instructions
        System.out.printf("     %-12s", currentInstruction.getInstructionId());
        System.out.printf(" %-13s", currentInstruction.getOperation());
        System.out.printf(" %-15s \n", currentInstruction.getAddress());
        if(currentInstruction.getOperation() == Operation.Start){
            //pageTables.add(new PageTable());
        }
    }

    public static void triggerAllInstruction(List<Instruction> instructions, List<PageTable> pageTables, int timer){
        for(;timer < instructions.size(); timer ++){
            triggerOneInstruction(instructions.get(timer), pageTables);
        }
    }

    public static class Instruction {

        private Integer instructionId;
        private Operation operation;
        private Integer address;

        public Instruction(int instructionId, String operation, int address) {
            this.instructionId = instructionId;
            this.operation = Operation.valueOf(operation);
            this.address = address;
        }

        public Integer getInstructionId(){
            return this.instructionId;
        }

        public Operation getOperation(){
            return this.operation;
        }

        public Integer getAddress(){
            return this.address;
        }

    }
    public static class Ram{
        private List<PageTable> processesInRam;

        public Ram(){
            //Most recently used is first
            processesInRam = new ArrayList<PageTable>();
        }
        public void addNewProcessToRam(PageTable newProcess) {
            if (processesInRam.size() == 4) {
                PageTableEntry lastAccessedPage = null;
                PageTable tableToRemove = null;
                for (PageTable currentPageTable : processesInRam) {
                    for (PageTableEntry currentPage : currentPageTable.getEntries()) {
                        if (lastAccessedPage == null) {
                            lastAccessedPage = currentPage;
                            tableToRemove = currentPageTable;
                        } else if (currentPage.getLastAccessTime().isBefore(lastAccessedPage.getLastAccessTime())) {
                            lastAccessedPage = currentPage;
                            if (tableToRemove != currentPageTable) {
                                tableToRemove = currentPageTable;
                            }
                        }
                    }
                }
                processesInRam.remove(tableToRemove);
                for (PageTableEntry currentPage : tableToRemove.getEntries()) {
                    if (currentPage.getPresentBit()) {
                        currentPage.setPresentBit(false);
                        currentPage.setFrameNumber(null);
                    }
                }
                //To be continued. Hierna volgt om de eerste 4 pages op te laden in het geheugen als nieuwe frames.
                //Eerst ervoor zorgen dat er bepaald wordt hoeveel frames er beschikbaar zijn (Als er 4 processen waren en 3 over na verwijderen van 1 dan is er
                //zijn er 3 frames ter beschikking. Als er 3 frames waren moet je eerst mbv LRU (least recently used) de pageframes/pagetable entry present bit en
                // framenummer op 0 en null zetten. (Voor elke PageTable 1 als er 3 zijn) en dan de pageframes van het nieuwe process erin laden.
            }
        }

            public void removeProcessFromRam(PageTable pageTableToRemove){
                processesInRam.remove(pageTableToRemove);
                pageTableToRemove.terminateProcess();
                //redistribute available pageframes
            }


    }

    public static class PageTable{
        private PageTableEntry[] entries;
        private Integer processId;
        public PageTable(int processId){
            this.processId = processId;
            entries = new PageTableEntry[16];
            for(int counter = 0; counter < entries.length; counter ++){
                entries[counter] = new PageTableEntry(counter);
            }
        }

        public int getProcessId(){
            return this.processId;
        }

        public PageTableEntry[] getEntries(){
            return this.entries;
        }
        public void terminateProcess(){
            for(PageTableEntry entry: entries){
                entry.setPresentBit(false);
                entry.setFrameNumber(null);
            }

    }
    }

    public static class PageTableEntry{
        private boolean presentBit;
        private boolean modifyBit;
        private LocalDateTime lastAccessTime;
        private Integer minRange;
        private Integer maxRange;
        private Integer frameNumber;
        public PageTableEntry(int frameNumber){
            this.frameNumber = frameNumber;
            this.presentBit = false;
            this.modifyBit = true;
        }
        public boolean getPresentBit(){
            return this.presentBit;
        }
        public void setPresentBit(Boolean newState){
            this.presentBit = newState;
        }
        public boolean getModifyBit(){
            return this.modifyBit;
        }
        public void setModifyBit(Boolean newState){
            this.modifyBit = newState;
        }
        public int getFrameNumber(){
            return this.frameNumber;
        }
        public void setFrameNumber(Integer frameNumber){
            this.frameNumber = frameNumber;
        }

        public LocalDateTime getLastAccessTime(){
            return this.lastAccessTime;
        }
        public void setLastAccessTime(LocalDateTime currentTime){
            lastAccessTime = currentTime;
        }
        public boolean checkAddressInRange(Integer address){
            if((minRange > address) && (address <= maxRange)){
                return true;
            }
            else{
                return false;
            }
        }

    }

    enum Operation{
        Start,
        Read,
        Write,
        Terminate
    }

}