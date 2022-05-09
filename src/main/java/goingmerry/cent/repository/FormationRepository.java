package goingmerry.cent.repository;

import goingmerry.cent.domain.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    //String back, int circle, String name, int x, int y, String position, String teamName

    // 검증 안 됨.
    @Query(value = "SELECT p.id, p.back, p.circle, p.name, p.x, p.y, p.position, p.team_name" +
            " FROM formation p WHERE p.team_name = :team_name ORDER BY circle ASC", nativeQuery = true)
    List<Formation> findFormationByTeamName(@Param("team_name") String teamName);

}
