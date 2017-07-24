/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.idf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import pl.koder95.ip.ActManager;
import static pl.koder95.ip.Main.BUNDLE;
import static pl.koder95.ip.Main.CSV_DEFAULT_CHARSET;
import static pl.koder95.ip.Main.DATA_DIR;
import static pl.koder95.ip.Main.READ_CSV_ERR_MESSAGE;
import static pl.koder95.ip.Main.READ_CSV_ERR_TITLE;
import static pl.koder95.ip.Main.showErrorMessage;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public enum Indices {

    LIBER_BAPTIZATORUM("Indeks ochrzczonych.csv"),
    LIBER_CONFIRMATORUM("Indeks bierzmowanych.csv"),
    LIBER_MATRIMONIORUM("Indeks zaślubionych.csv"),
    LIBER_DEFUNCTORUM("Indeks zmarłych.csv");
    
    private List<RealIndex> loaded;
    private final ActManager acts = new ActManager();
    private final String fileName, name;

    private Indices(String fileName) {
        this.fileName = fileName;
        name = fileName.substring(0, fileName.length()-4);
    }
    
    public Index get(int id) {
        return getReal(id).toVirtualIndex(this);
    }
    
    RealIndex getReal(int id) {
        return loaded.get(id-1);
    }

    public List<Index> getLoaded() {
        List<Index> virtual = new LinkedList<>();
        loaded.stream().forEach((ri)
                -> virtual.add(new VirtualIndex(ri.ID, this)));
        return virtual;
    }
    
    private int load(String line) {
        int id = loaded.size();
        if (id == 0) id = 1;
        RealIndex r = RealIndex.create(id, line);
        if (loaded.add(r)) {
            acts.create(r);
            return id;
        }
        return -1;
    }
    
    public void load() {
        loaded = new LinkedList<>();
        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(new FileInputStream(
                        new File(DATA_DIR, fileName)), CSV_DEFAULT_CHARSET))) {
            while (reader.ready()) load(reader.readLine());
        } catch (FileNotFoundException ex) {
            showErrorMessage(READ_CSV_ERR_MESSAGE, READ_CSV_ERR_TITLE, true);
        } catch (IOException ex) {
            showErrorMessage(BUNDLE.getString("ERR_EX_IO"),
                    BUNDLE.getString("ERR_EX_IO_TITLE"), true);
        }
        loaded = new ArrayList<>(loaded);
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public ActManager getActManager() {
        return acts;
    }
}
