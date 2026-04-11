package com.qi.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkBlogReq {

    List<CreateBlogRequest> bulkRequest;
}
