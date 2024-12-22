package org.but.feec.bds.api;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class BookView {
    private LongProperty bookId = new SimpleLongProperty();
    private StringProperty bookName = new SimpleStringProperty();
    private StringProperty isbn = new SimpleStringProperty();
    private LongProperty numPages = new SimpleLongProperty();
    private StringProperty intro = new SimpleStringProperty();
    private StringProperty releaseYear = new SimpleStringProperty();
    private StringProperty authorFirstName = new SimpleStringProperty();
    private StringProperty authorLastName = new SimpleStringProperty();

    // Getters and Setters with Properties
    public long getBookId() {
        return bookIdProperty().get();
    }

    public void setBookId(long bookId) {
        this.bookIdProperty().set(bookId);
    }

    public LongProperty bookIdProperty() {
        return bookId;
    }

    public String getBookName() {
        return bookNameProperty().get();
    }

    public void setBookName(String bookName) {
        this.bookNameProperty().set(bookName);
    }

    public StringProperty bookNameProperty() {
        return bookName;
    }

    public String getIsbn() {
        return isbnProperty().get();
    }

    public void setIsbn(String isbn) {
        this.isbnProperty().set(isbn);
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public long getNumPages() {
        return numPagesProperty().get();
    }

    public void setNumPages(long numPages) {
        this.numPagesProperty().set(numPages);
    }

    public LongProperty numPagesProperty() {
        return numPages;
    }

    public String getIntro() {
        return introProperty().get();
    }

    public void setIntro(String intro) {
        this.introProperty().set(intro);
    }

    public StringProperty introProperty() {
        return intro;
    }

    public String getReleaseYear() {
        return releaseYearProperty().get();
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYearProperty().set(releaseYear);
    }

    public StringProperty releaseYearProperty() {
        return releaseYear;
    }

    public String getAuthorFirstName() {
        return authorFirstNameProperty().get();
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstNameProperty().set(authorFirstName);
    }

    public StringProperty authorFirstNameProperty() {
        return authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastNameProperty().get();
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastNameProperty().set(authorLastName);
    }

    public StringProperty authorLastNameProperty() {
        return authorLastName;
    }
}
