package com.longrunpc.api.config;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.cohort.vo.TeamName;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SeedDataInitializer implements ApplicationRunner {

    private static final List<String> PART_NAMES = List.of("SERVER", "WEB", "iOS", "ANDROID", "DESIGN");
    private static final List<String> TEAM_NAMES = List.of("Team A", "Team B", "Team C");
    private static final String ADMIN_LOGIN_ID = "admin";
    private static final String ADMIN_PASSWORD = "admin1234";

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final DepositHistoryRepository depositHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Cohort cohort10 = seedCohort(10);
        Cohort cohort11 = seedCohort(11);

        seedParts(cohort10);
        seedParts(cohort11);
        seedTeams(cohort11);

        Cohort currentCohort = cohortRepository.findByGeneration(new Generation(currentGeneration))
            .orElse(cohort11);
        seedAdminAndDeposit(currentCohort);
    }

    private Cohort seedCohort(int generationValue) {
        Generation generation = new Generation(generationValue);
        return cohortRepository.findByGeneration(generation)
            .orElseGet(() -> Objects.requireNonNull(cohortRepository.save(
                Cohort.builder()
                    .generation(generation)
                    .cohortName(new CohortName(generationValue + "기"))
                    .build()
            )));
    }

    private void seedParts(Cohort cohort) {
        Set<String> existingPartNames = partRepository.findByCohortId(cohort.getId()).stream()
            .map(part -> part.getPartName().getValue())
            .collect(Collectors.toSet());

        PART_NAMES.stream()
            .filter(partName -> !existingPartNames.contains(partName))
            .forEach(partName -> Objects.requireNonNull(partRepository.save(
                Part.builder()
                    .partName(new PartName(partName))
                    .cohort(cohort)
                    .build()
            )));
    }

    private void seedTeams(Cohort cohort) {
        Set<String> existingTeamNames = teamRepository.findByCohortId(cohort.getId()).stream()
            .map(team -> team.getTeamName().getValue())
            .collect(Collectors.toSet());

        TEAM_NAMES.stream()
            .filter(teamName -> !existingTeamNames.contains(teamName))
            .forEach(teamName -> Objects.requireNonNull(teamRepository.save(
                Team.builder()
                    .teamName(new TeamName(teamName))
                    .cohort(cohort)
                    .build()
            )));
    }

    private void seedAdminAndDeposit(Cohort currentCohort) {
        Member admin = memberRepository.findByLoginId(new LoginId(ADMIN_LOGIN_ID))
            .orElseGet(() -> Objects.requireNonNull(memberRepository.save(
                Member.builder()
                    .loginId(new LoginId(ADMIN_LOGIN_ID))
                    .password(new Password(passwordEncoder.encode(ADMIN_PASSWORD)))
                    .memberName(new MemberName("admin"))
                    .phone(new Phone("01000000000"))
                    .role(MemberRole.ADMIN)
                    .status(MemberStatus.ACTIVE)
                    .build()
            )));

        CohortMember adminCohortMember = cohortMemberRepository.findByMemberIdAndCohortId(admin.getId(), currentCohort.getId())
            .orElseGet(() -> Objects.requireNonNull(cohortMemberRepository.save(
                CohortMember.createCohortMember(admin, currentCohort, null, null)
            )));

        if (depositHistoryRepository.findByCohortMemberId(adminCohortMember.getId()).isEmpty()) {
            DepositHistory depositHistory = DepositHistory.initialDeposit(adminCohortMember);
            Objects.requireNonNull(depositHistoryRepository.save(depositHistory));
        }
    }
}
