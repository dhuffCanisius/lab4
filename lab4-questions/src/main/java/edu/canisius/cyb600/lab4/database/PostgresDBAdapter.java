package edu.canisius.cyb600.lab4.database;

import edu.canisius.cyb600.lab4.dataobjects.Actor;
import edu.canisius.cyb600.lab4.dataobjects.Film;
import edu.canisius.cyb600.lab4.dataobjects.Category;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Postgres Implementation of the db adapter.
 */
public class PostgresDBAdapter extends AbstractDBAdapter {

    public PostgresDBAdapter(Connection conn) {
        super(conn);
    }

    @Override
    public Actor addActor(Actor actor) {
        String sql = "INSERT INTO actor (first_name, last_name, last_update) " +
                "VALUES (?, ?, now()) RETURNING actor_id, last_update";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, actor.getFirstName());
            pstmt.setString(2, actor.getLastName());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    actor.setActorId(rs.getInt("actor_id"));
                    actor.setLastUpdate(rs.getTimestamp("last_update"));
                    return actor;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Film> getFilmsInCategory(Category category) {
        String sql = "SELECT f.* FROM film f " +
                "JOIN film_category fc ON f.film_id = fc.film_id " +
                "JOIN category c ON fc.category_id = c.category_id " +
                "WHERE UPPER(c.name) = ?";

        List<Film> films = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName().toUpperCase()); // match formatting
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Film film = new Film();
                film.setFilmId(rs.getInt("film_id"));
                film.setTitle(rs.getString("title"));
                film.setDescription(rs.getString("description"));
                film.setReleaseYear(rs.getString("release_year"));
                film.setLanguageId(rs.getInt("language_id"));
                film.setRentalDuration(rs.getInt("rental_duration"));
                film.setRentalRate(rs.getDouble("rental_rate"));
                film.setLength(rs.getInt("length"));
                film.setReplacementCost(rs.getDouble("replacement_cost"));
                film.setRating(rs.getString("rating"));
                film.setSpecialFeatures(rs.getString("special_features"));
                film.setLastUpdate(rs.getTimestamp("last_update"));
                films.add(film);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return films;
    }

    @Override
    public List<String> getAllDistinctCategoryNames() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM category";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    @Override
    public List<Film> getAllFilmsWithALengthLongerThanX(int length) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM film WHERE length > ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, length);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Film film = new Film();
                film.setFilmId(rs.getInt("film_id"));
                film.setTitle(rs.getString("title"));
                film.setDescription(rs.getString("description"));
                film.setReleaseYear(rs.getString("release_year"));
                film.setLanguageId(rs.getInt("language_id"));
                film.setRentalDuration(rs.getInt("rental_duration"));
                film.setRentalRate(rs.getDouble("rental_rate"));
                film.setLength(rs.getInt("length"));
                film.setReplacementCost(rs.getDouble("replacement_cost"));
                film.setRating(rs.getString("rating"));
                film.setSpecialFeatures(rs.getString("special_features"));
                film.setLastUpdate(rs.getTimestamp("last_update"));
                films.add(film);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return films;
    }

    @Override
    public List<Actor> getActorsFirstNameStartingWithX(char firstLetter) {
        List<Actor> actors = new ArrayList<>();
        String sql = "SELECT * FROM actor WHERE LOWER(first_name) LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, (firstLetter + "%").toLowerCase());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Actor actor = new Actor();
                actor.setActorId(rs.getInt("actor_id"));
                actor.setFirstName(rs.getString("first_name"));
                actor.setLastName(rs.getString("last_name"));
                actor.setLastUpdate(rs.getTimestamp("last_update"));
                actors.add(actor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actors;
    }
}
