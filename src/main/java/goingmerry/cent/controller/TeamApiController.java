package goingmerry.cent.controller;

import goingmerry.cent.domain.Area;
import goingmerry.cent.domain.Team;
import goingmerry.cent.dto.TeamDto;
import goingmerry.cent.repository.AreaRepository;
import goingmerry.cent.repository.TeamRepository;
import goingmerry.cent.repository.UserRepository;
import goingmerry.cent.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/team")
public class TeamApiController {

    private final TeamService teamService;

    private final TeamRepository teamRepository;

    private final AreaRepository areaRepository;

    @RequestMapping(value = "/exist", method = RequestMethod.GET)
    public Map<String, String> existTeamName(@RequestBody Map<String, String> teamInfo) {

        Map<String, String> returnMap = new HashMap<>();

        String teamName = teamInfo.get("teamName");

        //errMsg같은 경우는 0,1의 코드 형태로 들어갈 수 있게 할 것.
        //DB에서 팀명으로 검색, 있는 팀명이라면 등록이 안 되게 하였다.
        if (teamService.isExistTeam(teamName)){
            returnMap.put("ErrorMsg", "이미 존재하는 팀입니다. 다른 팀명을 사용하세요.");
            returnMap.put("현재 팀명", teamName);
            log.info("Response : {}",returnMap);
        }
        else {
            returnMap.put("ApprovalMsg", "사용 가능한 팀명입니다.");
        }
        return returnMap;
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Map<String, String> createTeam(@RequestBody Map<String, String> teamInfo) {

        Map<String, String> returnMap = new HashMap<>();
        log.info("{}", teamInfo);

        //팀의 이름 또는 활동지역은 필수값이기 때문에 들어가지 않은 채로 요청이 오면 디비 입력이 안되게 했다.
        //사실 프론트에서 해줘서 굳이 필요 있는 로직은 아니긴 하다.
        boolean isTeamNameNull = teamService.isRequireValueNull(teamInfo).get("teamName");
        log.error(String.valueOf(isTeamNameNull));
        boolean isAreaNull = teamService.isRequireValueNull(teamInfo).get("area");
        log.error(String.valueOf(isAreaNull));
        if (isTeamNameNull || isAreaNull){
            if (isTeamNameNull) {
                returnMap.put("TeamName Is Null!!", "팀명은 필수 값 입니다.");

            }
            if (isAreaNull) {
                returnMap.put("Area Is Null!!", "활동 지역은 필수 값 입니다.");
            }
        }//필수 값을 모두 넣은 요청에 대해서 등록 또는 업데이트 로직 실행
        else {
        String teamName = teamInfo.get("teamName");
        String logo = teamInfo.get("logo");
        String intro = teamInfo.get("intro");
        String area = teamInfo.get("area");

        TeamDto team = TeamDto.builder()
                .teamName(teamName)
                .logo(logo)
                .intro(intro)
                .area(area)
                .build();

        //DB에서 팀명으로 검색, 있는 팀명이라면 등록이 안 되게 하였다.

                log.info("start save this team : " + team.getTeamName());
                returnMap = teamService.createTeam(team);
                log.info("Response : {}",returnMap);
        }

        return returnMap;

    }

    //삭제할 팀명이 있는 팀명인지 먼저 알아보는 것 필요
    //근데 팀장한테는 어짜피 팀 하나일 건데 굳이 있는 팀명인지 알아보는 게 필요할까라는 생각은 든다.
    //해당 메소드 서비스로 옮길 것.
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Map<String, String> deleteTeam(@RequestBody Map<String, String> deleteTeam) {

            String deleteTeamName = deleteTeam.get("teamName");
            var deleteTeamNameMap = new HashMap<String, String>();

        try {
            teamService.deleteTeam(deleteTeamName);
            deleteTeamNameMap.put("삭제된 팀명", deleteTeamName);
        }catch (Exception e) {
            log.error(e.getMessage());
            deleteTeamNameMap.put("ErrorMsg", e.getMessage());
        }
        return deleteTeamNameMap;
    }

    //팀의 다른 정보는 제하고, 팀의 이름만 리스트업하는 일반 쿼리 사용한 api
    @RequestMapping(value = "/list/team", method = RequestMethod.GET)
    public List<String> listAllTeam() {
//        return repository.findAllteamName();

        return teamRepository.findTeamName();
    }

    //지역을 도 / 시 값으로 전체 출력하는 api, 현재 json으로 list in map의 형식을 띄고 있다.
    @RequestMapping(value = "/allArea", method = RequestMethod.GET)
    public List<Map<String, String>> listAllCity() {

        List<Map<String, String>> returnMap = areaRepository.findAllArea();

        return returnMap;
    }

    //도명을 받아서 시를 추출해내는 api
    @RequestMapping(value = "/getcity", method = RequestMethod.GET)
    public List<String> listCityByRegion(@RequestBody Map<String, String> regionName) {

        String region = regionName.get("region");

        List<String> regionList = areaRepository.findCityByRegion(region);
        log.debug("{}", regionList);
        return regionList;
    }
}