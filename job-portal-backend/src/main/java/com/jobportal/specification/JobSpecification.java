package com.jobportal.specification;

import com.jobportal.entity.Job;
import com.jobportal.enums.JobType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    private JobSpecification() {}

    public static Specification<Job> filter(String keyword, String location, JobType jobType,
                                            Double minSalary, Integer maxExperience, String skill) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // sirf active jobs
            predicates.add(cb.isTrue(root.get("active")));

            if (StringUtils.hasText(keyword)) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.join("recruiter").get("companyName")), like)
                ));
            }
            if (StringUtils.hasText(location)) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.trim().toLowerCase() + "%"));
            }
            if (jobType != null) {
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }
            if (minSalary != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<Double>get("salaryMax"), minSalary));
            }
            if (maxExperience != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<Integer>get("experienceRequired"), maxExperience));
            }
            if (StringUtils.hasText(skill)) {
                query.distinct(true);
                Join<Job, String> skills = root.join("skillsRequired");
                predicates.add(cb.equal(cb.lower(skills), skill.trim().toLowerCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
