package com.dopplereffekt.dopperlertogo;


import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.identity.intents.Address;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

/**
 * Created by dsantagata
 */

/**
 * In dieser Klasse befinden sich statische Methoden, die die Library itextpdf benutzen. Diese brauchen wir um mit pdf's umzugehen.
 * Diese Klasse kann als Methodensammlung betrachtet werden. So ziemlich alle Methoden haben die gleiche aufgabe. Man nimmt den inhalt
 * des heruntergeladenen PDF's und extrahiert dessen Inhalt in verschiedenen Formen, wie z.B String[], String, char[]. Weiter sind Methoden enhalten die, denn
 * Inhalt überprüfen und Muster herausnehmen. Beispiele dafür sind die Methoden pdf2AdressStringArray(). Diese bearbeiteten Inhalte werden dan später zur
 * Ausgabe und Speicherung in der Webdatenbank benötigt.
 */

public class ConvertPDF {

Context context ;

    public static String pdf2string() throws IOException {



        //hier wird der Pfad angegeben wo sich das pdf befindet und wie es heisst
        String pdfPath = Environment.getExternalStorageDirectory() + "/" + MainActivity.foldername + "/" + MainActivity.pdfname;
        //Die Klasse PdfReader, auch aus der Library, kan den inhalt des PDF in String verwandeln.
        PdfReader reader = new PdfReader(pdfPath);
        StringWriter output = new StringWriter();

        //Try catch wird benötigt, im falle eines nicht möglichen ausführens der Methode.
        //Hier wenn z.B das File nicht am angegebenen Ort zu finden ist.
        try {
            output.append(PdfTextExtractor.getTextFromPage(reader, 1, new SimpleTextExtractionStrategy()));
        } catch (IOException e) {

        }
        //Aus einem StringWriter wird ein String erzeugt.
        String ausgang = output.toString();
        return ausgang;
    }


    public static char[] pdf2charArray() throws IOException {
        //hier wird der Pfad angegeben wo sich das pdf befindet und wie es heisst
        String pdfPath = Environment.getExternalStorageDirectory() + "/" + MainActivity.foldername  + "/" + MainActivity.pdfname;
        //Die Klasse PdfReader, auch aus der Library, kan den inhalt des PDF in String verwandeln.
        PdfReader reader = new PdfReader(pdfPath);
        StringWriter output = new StringWriter();

        //Try catch wird benötigt, im falle eines nicht möglichen ausführens der Methode.
        //Hier wenn z.B das File nicht am angegebenen Ort zu finden ist.
        try {
            output.append(PdfTextExtractor.getTextFromPage(reader, 1, new SimpleTextExtractionStrategy()));
        } catch (IOException e) {
        }
        //Unterschied zu pdf2string() ist nur die Rückgabe der Methode. Wir waren uns nicht sicher welche und ob wird diese
        //Methode benötigen.
        char[] rohArray = output.toString().toCharArray();

        return rohArray;
    }

    public static String[] pdf2AdressArray(){
        int suchzahl;
        String suchwort = "enthalten";
        String addressWorts;
        String[] addressWortsArray = null;
        String[] outputAddressArray = null;
        int k=0;
        try {
            //sucht den String nach dem suchwort "enthalten" ab und gibt den Index des ersten buchstaben des suchworts zurück
            suchzahl = ConvertPDF.pdf2string().indexOf(suchwort);

            //der Modistring neuer String, wo alles vor dem suchwort ("enthalten") wegnimmt und einen neuen String erzeugt
            addressWorts = ConvertPDF.pdf2string().substring(suchzahl + suchwort.length() + 1, ConvertPDF.pdf2charArray().length);

            //der modiString wird nach Zeilenumbrüche abgesucht und erstellt ein array mit einzelnen wörter
            addressWortsArray = addressWorts.split("\\n");

            outputAddressArray = new String[addressWortsArray.length/2];
            for(int i = 0 ;i<addressWortsArray.length; i=i+2){
                outputAddressArray[k] = addressWortsArray[i] + " " + addressWortsArray[i+1];
                k++;
            }

        }catch (IOException e){
           }
       return outputAddressArray;
    }


    //Diese Methode wird nur gebraucht, damit wir die Elemente des Arrays Dem GeoCoder übergeben werden können und wir Koordinaten zurück erhalten. So wie die Daten aus der Methode
    //pdf2AdressStringArray() kann google nicht nach den Koordinaten entschlüsseln.
    //15.03.15 Mir ist gerade aufgefallen, dass das PDF sehr günstig erstellt wurde. Wenn das PDF in ein String gewandelt wird und nachher nach Zeilenumbrüche ein Array erstellt wird,
    //ist immer der gerade Eintrag die Gemeinde und die ungeraden die Adresse. Nun gilt ein neues Array zu erstelle aber alle gemeinden werden nicht übernommen nur die adressen. 
    public static String[] pdf2AdressStringForAPI(){
        String[] hilfsArray;
        String[] addressArray = null;
        //Dieses Array hat die Einträg in der Rheienfolge (Gemeinde Plz Ort Strasse)
        addressArray = pdf2AdressArray();

        //nun besteht ds addresArray[] aus "plz + Dorf + (kanton) + Strasse" die Google api kann nach mehrern versuchen nich immer dieses Konstrukt auflösen.
        //abhilfe bringt wenn der Eintrag so aussieht (plz + strasse) dass konnte google immer in Koordinaten auflösen, sogar sehr abgelegene Orte. (9602 Bachstrasse)
        for(int i=0; i<addressArray.length; i++){
            hilfsArray = addressArray[i].split(" ");
            addressArray[i] = hilfsArray[0] + " " + hilfsArray[hilfsArray.length-1];
        }

        return addressArray;
    }


    //Diese Methode filtert das Datum aus dem PDF. Das Datum wird
    public static String getUpdateDate() {
        String date = null;
        String suchwort = "Kantonspolizei";
        String[] arrayWithoutWordwrap = null;

        try {
            //Aus dem kompleten String des PDF wird eine Array generiert, welches die elemente nach jedem Leerschlag bildet.
            arrayWithoutWordwrap = ConvertPDF.pdf2string().split("\\n");

            //Aus verschiedenen Tests, haben wir herausgefunden, dass nach dem Eintrag "Kantonspolizei" immer das Datum kommt.
            //Auf das sind wir auf der Suche. --> Falls mal das Design geändert wird, haben funktioniert diese Art nicht mehr.
            for(int i = 0; i<arrayWithoutWordwrap.length;i++){
                if (arrayWithoutWordwrap[i].equals(suchwort)){
                    date = arrayWithoutWordwrap[i+1];
                }
            }
        } catch (IOException e) {

            //   datum = "Datum ist nicht Aktuell";
        }
        return date;
    }


    //Diese Methode überprüft, ob ein PDF existiert and der Speicheradresse, welches dafür vorgesehen ist.
    //Falls der User das File löscht, wird es sofort wieder heruntergeladen.
    public static boolean pdfExists() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.foldername + "/" + MainActivity.pdfname);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }


    //Herausfinden, wie viele Blizer im PDF preisgegeben wurden. Die Anzahl kann nicht als Konstant angenommen werden, das Es wochen gibt, wo 8 Radaranlagen
    //und es Wochen gibt wo 10 Radargeräte vermerkt sind.
    public static int getNumberOfLighter() {
        String[] adressArray = pdf2AdressArray();
        int number = adressArray.length;
        return number;
    }


    //Für den Fall, dass ein PDF existiert aber ohne Inhalt.
    public static boolean contentIsIn() {
        try {
            if (pdf2string() != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }


 }