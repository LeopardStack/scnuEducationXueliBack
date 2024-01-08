package com.scnujxjy.backendpoint.util.dbf;

import com.linuxense.javadbf.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class DbfWriterUtil {

    public static void writeDbf(String filePath, DBFField[] fields, List<DbfRecord> records) {
        try (DBFWriter writer = new DBFWriter(new FileOutputStream(filePath), Charset.forName("GBK"))) {
            writer.setFields(fields);

            for (DbfRecord record : records) {
                writer.addRecord(record.toDbfRecord());
            }
        } catch (DBFException | IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] writeDbfToByteArray(DBFField[] fields, List<DbfRecord> records) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (DBFWriter writer = new DBFWriter(baos, Charset.forName("GBK"))) {
                writeRecordsToDbf(writer, fields, records);
                writer.write(); // Ensure data is written to the underlying stream
            } // Closing the writer
            return baos.toByteArray(); // Retrieve the byte array after the writer is closed
        } catch (DBFException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Shared method to write records
    private static void writeRecordsToDbf(DBFWriter writer, DBFField[] fields, List<DbfRecord> records) throws DBFException {
        writer.setFields(fields);
        for (DbfRecord record : records) {
            writer.addRecord(record.toDbfRecord());
        }
    }
}

