package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsResponse;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

	@Query("SELECT new ru.practicum.ViewStatsResponse(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
			"FROM EndpointHit e " +
			"WHERE e.timestamp BETWEEN :start AND :end " +
			"GROUP BY e.app, e.uri " +
			"ORDER BY COUNT(DISTINCT e.ip) DESC")
	List<ViewStatsResponse> findUniqueIpHitsForAllUris(@Param("start") LocalDateTime start,
	                                                   @Param("end") LocalDateTime end);

	@Query("SELECT new ru.practicum.ViewStatsResponse(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
			"FROM EndpointHit e " +
			"WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
			"GROUP BY e.app, e.uri " +
			"ORDER BY COUNT(DISTINCT e.ip) DESC")
	List<ViewStatsResponse> findUniqueIpHitsForSpecifiedUris(@Param("start") LocalDateTime start,
	                                                         @Param("end") LocalDateTime end,
	                                                         @Param("uris") List<String> uris);

	@Query("SELECT new ru.practicum.ViewStatsResponse(e.app, e.uri, COUNT(e.id)) " +
			"FROM EndpointHit e " +
			"WHERE e.timestamp BETWEEN :start AND :end " +
			"GROUP BY e.app, e.uri " +
			"ORDER BY COUNT(e.id) DESC")
	List<ViewStatsResponse> findTotalHitsForAllUris(@Param("start") LocalDateTime start,
	                                                @Param("end") LocalDateTime end);

	@Query("SELECT new ru.practicum.ViewStatsResponse(e.app, e.uri, COUNT(e.id)) " +
			"FROM EndpointHit e " +
			"WHERE e.uri IN :uris AND e.timestamp BETWEEN :start AND :end " +
			"GROUP BY e.app, e.uri " +
			"ORDER BY COUNT(e.id) DESC")
	List<ViewStatsResponse> findTotalHitsForSpecifiedUris(@Param("start") LocalDateTime start,
	                                                      @Param("end") LocalDateTime end,
	                                                      @Param("uris") List<String> uris);
}