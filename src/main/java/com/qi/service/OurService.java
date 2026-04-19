package com.qi.service;

import com.qi.dto.ProjectResponse;
import com.qi.modal.ProjectImage;
import com.qi.modal.RecentProjects;
import com.qi.repository.ProjectRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OurService {

    private final ProjectRepo projectRepo;
    private final S3Service s3Service;
    @CacheEvict(value = "projects", allEntries = true)
    public String uploadProject(String title, String desc, List<MultipartFile> files){
        RecentProjects projects = new RecentProjects();
        projects.setProjectTitle(title);
        projects.setProjectDescription(desc);
        List<ProjectImage> images = files.stream().map(file->{
            String key = s3Service.uploadFile(file);
            ProjectImage image = new ProjectImage();
            image.setProject(projects);
            image.setImageUrl(key);
            return image;
        }).toList();
        projects.setImages(images);
        projectRepo.save(projects);
        return "upload success";
    }
    @CacheEvict(value = "projects", allEntries = true)
    public String deleteProject(String id){
        RecentProjects project = projectRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
       project.getImages().forEach(img-> s3Service.deleteFile(img.getImageUrl()));

       projectRepo.delete(project);
       return "deleted successfully";
    }
    @Cacheable(
            value = "projects",
            key = "#cursor + '_' + #size",
            unless = "#result == null || #result.isEmpty()"
    )
public List<ProjectResponse> getProjects(String cursor,int size){
   Instant cursorTime;
    if (cursor == null || cursor.isEmpty()) {
        cursorTime = null;
    } else {
        try {
            cursorTime = Instant.parse(cursor);
        } catch (Exception e) {
            throw new RuntimeException("Invalid cursor format");
        }
    }
   List<RecentProjects> projects = projectRepo.findProjectsPage(cursorTime,PageRequest.of(0,size));
   return projects.stream().map(p->{
       ProjectResponse response = new ProjectResponse();
       response.setId(p.getId());
       response.setTitle(p.getProjectTitle());
       response.setDescription(p.getProjectDescription());
       response.setCreatedAt(p.getCreatedAt());
       List<String> urls =(p.getImages() != null)? p.getImages().stream().map(img->s3Service.generateUrl(img.getImageUrl())).toList():List.of();
       response.setImageUrls(urls);
       return response;
   }).toList();
}
}
