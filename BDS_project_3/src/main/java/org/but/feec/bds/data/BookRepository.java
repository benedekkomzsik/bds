package org.but.feec.bds.data;

import org.but.feec.bds.api.BookView;
import org.but.feec.bds.config.DataSourceConfig;
import org.but.feec.bds.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    public List<BookView> getBookView() {
        try (Connection connection = DataSourceConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT " +
                     "    b.book_id, " +
                     "    b.book_name, " +
                     "    a.first_name AS author_first_name," +
                     "    a.last_name AS author_last_name, " +
                     "    b.isbn, " +
                     "    b.release_year, " +
                     "    b.intro, " +
                     "    b.num_pages " +
                     "FROM " +
                     "    bds.book_has_author bha " +
                     "JOIN " +
                     "    bds.book b ON bha.book_id = b.book_id " +
                     "JOIN " +
                     "    bds.author a ON bha.author_id = a.author_id ;"
             );
             ResultSet resultSet = preparedStatement.executeQuery();) {
            List<BookView> bookBasicViews = new ArrayList<>();
            while (resultSet.next()) {
                bookBasicViews.add(mapToBookView(resultSet));
            }
            return bookBasicViews;
        } catch (SQLException e) {
            System.out.println("Nemozno nacitat knihu.");
            e.printStackTrace();
        }
        return null;
    }

    public void insertBook(String bookName, String isbn, int numPages, String intro, String releaseYear, String authorFirstName, String authorLastName) {
        String insertSQL = "WITH author_data AS (" +
                "    SELECT author_id FROM bds.author WHERE first_name = ? AND last_name = ?" +
                ") " +
                "INSERT INTO bds.book (" +
                "    book_name, " +
                "    isbn, " +
                "    num_pages, " +
                "    intro, " +
                "    release_year, " +
                "    author_id" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, (SELECT author_id FROM author_data));";

        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, authorFirstName);
            preparedStatement.setString(2, authorLastName);
            preparedStatement.setString(3, bookName);
            preparedStatement.setString(4, isbn);
            preparedStatement.setInt(5, numPages);
            preparedStatement.setString(6, intro);
            preparedStatement.setString(7, releaseYear);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating book failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating book failed, operation on the database failed.", e);
        }
    }


    public List<BookView> getBooksByName(String bookName){
        try (Connection connection = DataSourceConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT " +
                             "b.book_id, b.book_name, b.isbn, b.num_pages, b.intro, b.release_year, " +
                             "a.first_name AS author_first_name, a.last_name AS author_last_name " +
                             "FROM bds.book b " +
                             "JOIN bds.book_has_author bha ON b.book_id = bha.book_id " +
                             "JOIN bds.author a ON bha.author_id = a.author_id " +
                             "WHERE b.book_name = ?")){
            preparedStatement.setString(1, bookName);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<BookView> bookViews = new ArrayList<>();
            while (resultSet.next()) {
                bookViews.add(mapToBookView(resultSet));
            }
            return bookViews;
        } catch (SQLException e) {
            System.out.println("Failed to load books by name.");
            e.printStackTrace();
        }
        return null;
    }

    private BookView mapToBookView(ResultSet rs) throws SQLException {
        BookView bookView = new BookView();
        bookView.setBookId(rs.getLong("book_id"));
        bookView.setBookName(rs.getString("book_name"));
        bookView.setIsbn(rs.getString("isbn"));
        bookView.setNumPages(rs.getLong("num_pages"));
        bookView.setIntro(rs.getString("intro"));
        bookView.setReleaseYear(rs.getString("release_year"));
        bookView.setAuthorFirstName(rs.getString("author_first_name"));
        bookView.setAuthorLastName(rs.getString("author_last_name"));
        return bookView;
    }
}
