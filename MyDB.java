/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mydb;

import java.util.List;
import java.io.File;
import myDBDriver.Config;
import myDBDriver.Record;
import myDBDriver.Table;

/**
 *
 * @author g.ijewski
 */
public class MyDB {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CreateTestTable("test2.mdb");
        FillTable("test2.mdb");
        Query("test2.mdb");
    }
    
    private static void CreateTestTable(String filename) {
        try {
            System.out.println("------------------------------------------");
            System.out.println("Enter CreateTestTable()");
            
            // Delete old table
            File f = new File(filename);
            if(f.exists())
                f.delete();
            
            Table db = new Table(filename);
            
            db.AddIntField("Alter");
            db.AddBlobField(512, "askjsdksdk");     
            db.AddBlobField(64, "Name");

            System.out.println("Flush");
            db.FlushTable(); // Writes to file
        }
        catch(Exception E) {
            System.out.println(E);
        }
    }
    
    private static void FillTableHelper1(Table db) {
        byte[] val = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0x0a};
        byte[] val2 = {0x0a, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};

        Record r = new Record();
        r.AddField("Alter", 42);
        r.AddField("askjsdksdk", 512, val);                
        r.AddField("Name", 64, val2);
        
        db.AddRecord(r);
    }
    
    private static void FillTableHelper2(Table db) {
        byte[] val = {0x41, 0x42, 0x43,
            0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
            0x41, 0x42, 0x43,};

        Record r = new Record();
        r.AddField("Alter", 1337);
        r.AddField("askjsdksdk", 512, val);                
        r.AddField("Name", 64, val);
        
        db.AddRecord(r);
    }
    
    private static void FillTableHelper3(Table db) {
        if(0 == db.GetNumberOfRecords())
            return;
        
        Record rOriginal = db.GetRecord(0);
        
        // Get data
        int alter = rOriginal.GetIntField("Alter");
        byte[] askj = rOriginal.GetBlobField("askjsdksdk");
        
        // Change data
        alter++;
        byte name[] = {'N', 'e', 'w'};
        askj[0]++;
        
        // Insert new data
        Record r = new Record();
        r.AddField("Alter", alter);
        r.AddField("askjsdksdk", 512, askj);                
        r.AddField("Name", 64, name);
        
        db.AddRecord(r);
    }
    
    
    
    private static void FillTable(String filename) {
        try {
            System.out.println("------------------------------------------");
            System.out.println("Enter FillTable()");
            
            Table db = new Table(filename);
            FillTableHelper1(db);
            FillTableHelper2(db);
            FillTableHelper3(db);
            
            db.FlushTable();
        }
        catch(Exception E) {
            System.out.println("Fill:" + E);
        }        
    }
    
    private static void PrintByteArray(byte[] a, String padding) {
        System.out.print(padding + "{");
        for(int i = 0; i < a.length; i++)
            System.out.print(a[i] + ",");
        System.out.println("}");
    }
    
    private static void PrintResults(List<Record> records) {
        for(Record r: records) {
            System.out.println("\tAlter:" + r.GetIntField("Alter"));
            PrintByteArray(r.GetBlobField("Name"), "\t");
            System.out.println("\t---");
        }
    }
    
    private static void Query(String filename) {
        try {
            System.out.println("------------------------------------------");
            System.out.println("Enter Query()");
            
            Table db = new Table(filename);
            
            db.PrintTableLayout();
            
            System.out.println("Search by blob");
            byte[] val = {0x0a, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
            List<Record> results = db.Query("Name", val);
            if(!results.isEmpty())
            {
                PrintResults(results);
                results.clear();
            }
            
            System.out.println("Search by int");
            results = db.Query("Alter", 1337);
            if(!results.isEmpty())
            {
                PrintResults(results);
                results.clear();
            }
            
            System.out.println("Search by int >= ");
            Config cfg = Config.GetInstance();
            results = db.Query("Alter", 42, cfg.compareGreaterEqual);
            if(!results.isEmpty())
            {
                PrintResults(results);
                results.clear();
            }
        }
        catch(Exception E) {
            System.out.println(E);
        }
    }
}
