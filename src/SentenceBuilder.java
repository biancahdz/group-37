/**
 * File: SentenceBuilder.java
 * Description: Entry point for the SentenceBuilder application.
 *
 * Author: Cortland Kimzey
 * Created: 2026-03-15
 * Last Modified: 2026-03-16
 *
 * Version: 1.0
 *
 * Notes:
 * - Quick entry point no UI for 1.0
 */

import data_layer.Database;
import data_layer.TxtFileReader;

public class SentenceBuilder
{
    public static void main(String[] args)
    {
        TxtFileReader fileReader = new TxtFileReader("JubileeHall.txt");
        fileReader.processTxt();
    }
}