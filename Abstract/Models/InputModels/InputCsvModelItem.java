package Abstract.Models.InputModels;

import com.opencsv.bean.CsvBindByPosition;

import java.lang.reflect.Field;
import java.util.Objects;

public class InputCsvModelItem {

    public InputCsvModelItem() {
    }

    @CsvBindByPosition(position = 0)
    private String columnA;

    @CsvBindByPosition(position = 1)
    private String columnB;

    @CsvBindByPosition(position = 2)
    private String columnC;

    @CsvBindByPosition(position = 3)
    private String columnD;

    @CsvBindByPosition(position = 4)
    private String columnE;

    @CsvBindByPosition(position = 5)
    private String columnF;

    @CsvBindByPosition(position = 6)
    private String columnG;

    @CsvBindByPosition(position = 7)
    private String columnH;

    @CsvBindByPosition(position = 8)
    private String columnI;

    @CsvBindByPosition(position = 9)
    private String columnJ;

    @CsvBindByPosition(position = 10)
    private String columnK;

    @CsvBindByPosition(position = 11)
    private String columnL;

    @CsvBindByPosition(position = 12)
    private String columnM;

    @CsvBindByPosition(position = 13)
    private String columnN;

    @CsvBindByPosition(position = 14)
    private String columnO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputCsvModelItem that = (InputCsvModelItem) o;
        return Objects.equals(columnA, that.columnA) &&
                Objects.equals(columnB, that.columnB) &&
                Objects.equals(columnC, that.columnC) &&
                Objects.equals(columnD, that.columnD) &&
                Objects.equals(columnE, that.columnE) &&
                Objects.equals(columnF, that.columnF) &&
                Objects.equals(columnG, that.columnG) &&
                Objects.equals(columnH, that.columnH) &&
                Objects.equals(columnI, that.columnI) &&
                Objects.equals(columnJ, that.columnJ) &&
                Objects.equals(columnK, that.columnK) &&
                Objects.equals(columnL, that.columnL) &&
                Objects.equals(columnM, that.columnM) &&
                Objects.equals(columnN, that.columnN) &&
                Objects.equals(columnO, that.columnO);
    }

    @Override
    public int hashCode() {
        try {
            return Objects.hash(columnA)
                    + Objects.hash(columnB)
                    + Objects.hash(columnC)
                    + Objects.hash(columnD)
                    + Objects.hash(columnE)
                    + Objects.hash(columnF)
                    + Objects.hash(columnG)
                    + Objects.hash(columnH)
                    + Objects.hash(columnI)
                    + Objects.hash(columnK)
                    + Objects.hash(columnL)
                    + Objects.hash(columnM)
                    + Objects.hash(columnN)
                    + Objects.hash(columnO)
                    ;
        } catch (Exception ex) {
            return Objects.hash(columnA, columnB, columnC, columnD, columnE, columnF, columnG, columnH, columnI, columnJ, columnK, columnL, columnM, columnN, columnO);
        }

    }

    public void setValue(String columnName, String value) {
        try {
            Field field = InputCsvModelItem.class.getDeclaredField(columnName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public InputCsvModelItem copy() {
        InputCsvModelItem copiedItem = new InputCsvModelItem();
        copiedItem.columnA = this.columnA;
        copiedItem.columnB = this.columnB;
        copiedItem.columnC = this.columnC;
        copiedItem.columnD = this.columnD;
        copiedItem.columnE = this.columnE;
        copiedItem.columnF = this.columnF;
        copiedItem.columnG = this.columnG;
        copiedItem.columnH = this.columnH;
        copiedItem.columnI = this.columnI;
        copiedItem.columnJ = this.columnJ;
        copiedItem.columnK = this.columnK;
        copiedItem.columnL = this.columnL;
        copiedItem.columnM = this.columnM;
        copiedItem.columnN = this.columnN;
        copiedItem.columnO = this.columnO;

        return copiedItem;
    }

    public String getColumnA() {
        return columnA;
    }

    public String getColumnB() {
        return columnB;
    }

    public String getColumnC() {
        return columnC;
    }

    public String getColumnD() {
        return columnD;
    }

    public String getColumnE() {
        return columnE;
    }

    public String getColumnF() {
        return columnF;
    }

    public String getColumnG() {
        return columnG;
    }

    public String getColumnH() {
        return columnH;
    }

    public String getColumnI() {
        return columnI;
    }

    public String getColumnJ() {
        return columnJ;
    }

    public String getColumnK() {
        return columnK;
    }

    public String getColumnL() {
        return columnL;
    }

    public String getColumnM() {
        return columnM;
    }

    public String getColumnN() {
        return columnN;
    }

    public String getColumnO() {
        return columnO;
    }
}