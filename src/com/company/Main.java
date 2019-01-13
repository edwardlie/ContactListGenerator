/*******************************************************************************
 Created by Edward Lie
 *******************************************************************************/

package com.company;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    /*******************************************************************************
     Function Declaration: Process address so that first characters of every word are capitalized,
     there are no commas, and period is added
     *******************************************************************************/
    public static String modifyAddress(String string) {
        //break String down into characters for easier processing
        char[] chars = string.toLowerCase().toCharArray();
        //create smaller character array in case a comma appears in the String object
        char[] updatedCharsDueToComma = new char[chars.length - 1];
        //BEGIN LOOP
        for (int i = 0; i < chars.length; i++) {
            //capitalize all strings after white spaces
            if (Character.isWhitespace(chars[i]) && i != chars.length - 1) {
                chars[i + 1] = Character.toUpperCase(chars[i + 1]);
            }
            //add periods if character is space and iterator is in last position
            else if (Character.isWhitespace(chars[i])) {
                chars[i] = '.';
            //add period if iterator is pointing to last character of string AND character is a letter
            } else if (i == chars.length - 1 && Character.isLetter(chars[i])) {
                char[] updatedChars = new char[chars.length + 1];
                //to increase the size of the array, a new array is created with a larger size, and a copy of all old
                //values is put into the new array
                System.arraycopy(chars, 0, updatedChars, 0, chars.length);
                //make '.' the last character in the string
                updatedChars[i + 1] = '.';
                return String.valueOf(updatedChars);
            } else if (chars[i] == ',') {
                for (int j = i; j < chars.length - 1; j++) {
                    chars[j] = chars[j + 1]; //replace characters with next characters, stop before going out of bounds
                }
                //all characters have been pushed up from the point where the ',' is, copy everything except the very last element
                System.arraycopy(chars, 0, updatedCharsDueToComma, 0, chars.length - 1);
                //updatedCharsDueToComma effectively passed by reference (same array)
                chars = modifySmallerArray(updatedCharsDueToComma, i);
            }
        }
        //END LOOP
        return String.valueOf(chars);
    }

    /*******************************************************************************
     Function Declaration: Processing of character array after comma
     *******************************************************************************/
    public static char[] modifySmallerArray(char[] string, int resumePlace) {
        char[] updatedCharsDueToComma = new char[string.length - 1];
        //BEGIN LOOP
        for (int i = resumePlace; i < string.length; i++) {
            //capitalize all strings after white spaces
            if (Character.isWhitespace(string[i]) && i != string.length - 1) {
                string[i + 1] = Character.toUpperCase(string[i + 1]);
            }
            //add periods if character is space
            else if (Character.isWhitespace(string[i])) {
                string[i] = '.';
            } else if (i == string.length - 1 && Character.isLetter(string[i])) {
                //add period if iterator at last character of string AND character is a letter
                char[] updatedChars = new char[string.length + 1];
                //to increase the size of the array, a new array is created with a larger size and a copy of all old
                //values into the new array
                System.arraycopy(string, 0, updatedChars, 0, string.length);
                updatedChars[i + 1] = '.';
            } else if (string[i] == ',') {
                //replace the , with the next character in original array (space)
                for (int j = i; j < string.length - 1; j++) {
                    //replace characters with next characters, stop before going out of bounds
                    string[j] = string[j + 1];
                }
                //all characters have been pushed up from the point where the ',' is, now copy everything except the very last element
                System.arraycopy(string, 0, updatedCharsDueToComma, 0, string.length - 1);
                updatedCharsDueToComma = modifySmallerArray(updatedCharsDueToComma, i);
                string = updatedCharsDueToComma;
            }
        }
        //END LOOP
        return string;
    }

    /*******************************************************************************
     Function Declaration: Capitalizes the first letter of a word, in this case, the city
     *******************************************************************************/
    public static String capitalizeCity(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = Character.toUpperCase(chars[i]); //if the first letter in the string is a
            // character, capitalize it
            break;
        }
        return String.valueOf(chars);
    }

    /*******************************************************************************
     Class Declaration: Holds contact information for each member
     *******************************************************************************/
    static class Contact {
        String firstName, lastName, address, city, state;
        int age;
    }

    /*******************************************************************************
     Main Function
     *******************************************************************************/
    public static void main(String[] args) throws IOException {
        HashMap<String, List<Contact>> hashMapLastNametoContacts = new HashMap<String, List<Contact>>();
        HashMap<String, Integer> hashMapLastNameToSize = new HashMap<String, Integer>();
        try {
            int counter = 0;
            BufferedReader br = new BufferedReader(new FileReader("names.txt")); //read file
            String line; //data structure to read each line of the file into
            ArrayList<Contact> contactsList = new ArrayList();//hold all contacts that will be displayed
            while ((line = br.readLine()) != null) {
                //regular expression to capture content between double quotes
                Pattern p = Pattern.compile("\"([^\"]*)\"");
                Matcher m = p.matcher(line);
                Contact person = new Contact();
                while (m.find()) {
                    if (counter == 0) {
                        //first reading of element that matches pattern
                        person.firstName = m.group(1);
                    } else if (counter == 1) {
                        //second reading of element that matches pattern
                        person.lastName = m.group(1);
                    } else if (counter == 2) {
                        //third reading of element that matches pattern
                        person.address = modifyAddress(m.group(1));
                    } else if (counter == 3) {
                        //fourth reading of element that matches pattern
                        person.city = capitalizeCity(m.group(1));
                    } else if (counter == 4) {
                        //fifth reading of element that matches pattern
                        person.state = m.group(1).toUpperCase();
                    } else {
                        //sixth reading of element that matches pattern
                        person.age = Integer.parseInt(m.group(1));
                    }
                    counter++;
                }
                //reset counter
                counter = 0;
                //add contacts to list with ages of 18 or greater only
                contactsList.add(person);
            }

            /*******************************************************************************
             HashMap for list of contacts per household
             *******************************************************************************/
            for (int i = 0; i < contactsList.size(); i++) {
                if (!hashMapLastNametoContacts.containsKey(contactsList.get(i).lastName)) {
                    List<Contact> list = new ArrayList<>();
                    list.add(contactsList.get(i));
                    hashMapLastNametoContacts.put(contactsList.get(i).lastName, list);
                } else {
                    hashMapLastNametoContacts.get(contactsList.get(i).lastName).add(contactsList.get(i));
                }
            }

            /*******************************************************************************
             HashMap for number of occupants per household
             *******************************************************************************/
            for (int i = 0; i < contactsList.size(); i++) {
                if (!hashMapLastNameToSize.containsKey(contactsList.get(i).lastName)) {
                    hashMapLastNameToSize.put(contactsList.get(i).lastName, 1);
                } else {
                    hashMapLastNameToSize.put(contactsList.get(i).lastName, hashMapLastNameToSize.get(contactsList.get(i).lastName) + 1);
                }
            }

            /*******************************************************************************
             Sort contact list alphabetically by last name and then first name in ascending order
             *******************************************************************************/
            Collections.sort(contactsList, new Comparator() {

                public int compare(Object o1, Object o2) {
                    //compares both strings alphabetically by last name
                    String x1 = ((Contact) o1).lastName;//cast to Contact object
                    String x2 = ((Contact) o2).lastName;
                    int sortCompare = x1.compareTo(x2);

                    if (sortCompare != 0) {
                        return sortCompare;
                    }
                    //compares both strings alphabetically by first name
                    String x3 = ((Contact) o1).firstName;
                    String x4 = ((Contact) o2).firstName;
                    return x3.compareTo(x4);
                }
            });

                /*******************************************************************************
                 Create sorted hashMap based on key (last name)
                 *******************************************************************************/
                Map<String, List<Contact>> masterLastNametoContactsHashMap = new TreeMap<>(hashMapLastNametoContacts);

                /*******************************************************************************
                 Display household, number of occupants, and members of household 18 years old or older
                 *******************************************************************************/
                PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
                for (Map.Entry entry : masterLastNametoContactsHashMap.entrySet()) {
                    out.println("Household: " + entry.getKey()
                            + "\nNumber of Occupants: " + hashMapLastNameToSize.get(entry.getKey())
                            + "\nOccupants 18 years old or older: ");
                    for (int i = 0; i < contactsList.size(); i++) {
                        if (contactsList.get(i).age >= 18 && entry.getKey().equals(contactsList.get(i).lastName)){
                            out.println("\nName: " + contactsList.get(i).firstName
                                    + "\nLast Name: " + contactsList.get(i).lastName
                                    + "\nAddress: " + contactsList.get(i).address
                                    + "\nCity, State: " + contactsList.get(i).city + ", " + contactsList.get(i).state
                                    + "\nAge: " + contactsList.get(i).age);
                        }
                    }
                    out.println("///////////////////////////////////////////////");
            }
            System.out.print("Success: output file created and written to");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}


