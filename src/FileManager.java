import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.IntStream;
import content.publicAPI.*;


/**
 * Created by josh on 5/26/17.
 * Manages all of the serialized school files for a selected directory.
 *
 * When you want to serialize an object, call safelySerialize()
 * When you want to retrieve an object, find it in getObjectList()
 *
 * FileManager takes care of the rest.
 *
 * See notes file for reason for using extended on interface
 */
public class FileManager <T extends Serializable> {

    public ArrayList<T> getObjectList() {
        return objectList;
    }

    // Stores previously deserialized schools of current execution and in the desired directory
    private ArrayList<T> objectList;

    // Stores all of the school objects stored in the current directory
    private List<File> fileList = new ArrayList<>();

    // A companion object to fileList used to maintain synchronous lists between fileList and objectList
    private List<File> syncList; // remove?

    // Stores the working directory of FileManager instance
    private File directory;

    // Equal to the root folder of the Java project
    private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");

    // Used for filtering directory file's to the type used by your application.
    private String extension; // Without the dot

    public FileManager(String extension) {
        this(extension, new File(DEFAULT_DIRECTORY));
    }
    

    public FileManager(String extension, File directory) {
        this.extension = extension;
        objectList = new ArrayList<>();
        // Sets the default directory as the root folder
        this.directory = directory;
        if (!directory.exists()) directory.mkdirs();
        updateFiles();
        //updateAllObjects();
        //fileList = new ArrayList<>();
        updateAllObjects();
    }

    /*
    public FileManager(String extension, File directory) {
        this(extension);
        this.directory = directory;
    }*/


    /**
     * Scrubs the current directory for all files of the specified extension. Instance variable fileList is then
     * populated with File objects representing each matching file.
     * @return Whether or not it found any files
     */
    public boolean updateFiles() {
        /*
        The following lambda expression replaces an entire anonymous FilenameFilter class constructor as follows:

        new FilenameFilter() {
              @Override
              public boolean accept(File file, String s) {
                  return s.toLowerCase().endsWith(extension);
              }
        });

        The lambda is smart enough to interpret the both the method signature, and the containing class, all from the
        context and the provided method body. Java 8 scares me sometimes. */
        System.out.println("Checking for files");
        File[] files =
                Arrays.stream(directory.listFiles())
                .filter(f-> f.getName().endsWith(extension)).toArray(File[]::new);
        if (files.length > 0) {
            System.out.println("There are files");
            //Arrays.stream(files).forEach(it -> fileList.add(it));
            fileList = new ArrayList<File>(Arrays.asList(files));
            //System.out.println("fileList size: " + fileList.size());
            for (File f : fileList) {
                System.out.println(f.getName());
            }
            syncList = fileList;
            return true;
        } else {
            fileList = new ArrayList<>();
        }
        return false;
    }

    /**
     * Deserializes all file of extension within directory.
     */
    public void updateAllObjects() {
        //System.out.println("updating objects");
        //IntStream.range(0, fileList.size()).forEach(it -> deserialize(it));
        //System.out.println("fileList size: " + fileList.size());

        /*
         * A note on the inefficient loop:
         * Inevitably, starting with a for each and then scrubbing for the index would be slower than looking through
         * the index to begin with and then deserialziing that index. However, understanding that that
         */
        int index = 0;
        //if (fileList!= null) {
            while (index < fileList.size()) {
                try {

                    deserialize(index);
                } catch (EOFException e) {

                }
                index ++;
            }
        //}
        //System.out.println("Num Objects: " + objectList.size());
    }

    // Syncs fileList 1:1 with potential object list
    private void syncFiles() { // I think this is pointless?
        if (syncList != fileList) {
            fileList = new ArrayList<>(syncList.size());
            Collections.copy(syncList, fileList);
        }
    }

    // Throws operator?
    public void deserialize(int index) throws EOFException{
        //System.out.println("we deserialziing");
        File src = fileList.get(index);
        System.out.println(src.getName());
        try {

            FileInputStream f_in = new FileInputStream(src);

            // Read object using ObjectInputStream
            //System.out.println("updating object 1");
            ObjectInputStream obj_in = new ObjectInputStream(f_in);

            Object output = obj_in.readObject();

            //T data = null;
            ///  data = (T) output;

            objectList.add((T) output); // this code will throw an EOF exception if its not fitting the class
            // essentially, it covers the functionality of the instanceof operator
            // if it is an instance, this code runs, otherwise it throws an EOF
            //System.out.println("deserialzied success");
            /*Class type = ((T) new Object()).getClass();
            if (type.isInstance(output)) {
                objectList.add((T)output );
                //System.out.println("deserialzied success");
            } else {
                //System.out.println("not a valid object");
            }*/

        } catch (EOFException e) {
            //System.out.println("not the right class");
            /* This is a now depreciated attempt at syncing references vs real data
            syncList = new ArrayList<>(fileList.size());
            Collections.copy(syncList, fileList);
            syncList.remove(index); // keep the file list and the object list synced*/

            fileList.remove(index);
            throw new EOFException(); // pass error handling onto library implementation

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //System.out.println("We failed");
        }
    }

    public void serialize(T src, String name) {
        if (!objectList.contains(src)) {
            objectList.add(src);
            fileList.add(new File(name));
        }

        // Write to disk with FileOutputStream
        try {
            FileOutputStream f_out = new FileOutputStream(directory + "/" + name + "." + extension);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(f_out);
            objectOutputStream.writeObject(src);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Attempts to save in directory with name, returns false if conflicting name
     * @param src the object to be serialized
     * @param name the filename used to save on disk
     * @return
     */
    public boolean safelySerialize(T src, String name) {
        // If there are no duplicate names
        if (fileList.stream().map(it -> it.getName()).noneMatch(it -> it == name)) {
            serialize(src, name);
            return true;
        }
        return false;
    }

    public File getDirectory() {
        return directory;
    }

    public File getAbsoluteFile(int index) {
        return new File(directory + "/" + fileList.get(index).getAbsoluteFile() + "." + extension);
    }
}
