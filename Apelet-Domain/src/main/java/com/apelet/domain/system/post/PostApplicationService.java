package com.apelet.domain.system.post;

import com.apelet.common.core.page.PageDTO;
import com.apelet.domain.common.command.BulkOperationCommand;
import com.apelet.domain.system.post.command.AddPostCommand;
import com.apelet.domain.system.post.command.UpdatePostCommand;
import com.apelet.domain.system.post.db.SysPostEntity;
import com.apelet.domain.system.post.db.SysPostService;
import com.apelet.domain.system.post.dto.PostDTO;
import com.apelet.domain.system.post.model.PostModel;
import com.apelet.domain.system.post.model.PostModelFactory;
import com.apelet.domain.system.post.query.PostQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author valarchie
 */
@Service
@RequiredArgsConstructor
public class PostApplicationService {

    private final PostModelFactory postModelFactory;

    private final SysPostService postService;

    public PageDTO<PostDTO> getPostList(PostQuery query) {
        Page<SysPostEntity> page = postService.page(query.toPage(), query.toQueryWrapper());
        List<PostDTO> records = page.getRecords().stream().map(PostDTO::new).collect(Collectors.toList());
        return new PageDTO<>(records, page.getTotal());
    }

    /**
     * 查询满足条件的所有岗位，不分页
     * @param query 查询条件
     * @return 满足查询条件的岗位列表
     * @author Kevin Zhang
     * @date 2023-10-02
     */
    public List<PostDTO> getPostListAll(PostQuery query) {
        List<SysPostEntity> all = postService.list(query.toQueryWrapper());
        List<PostDTO> records = all.stream().map(PostDTO::new).collect(Collectors.toList());
        return records;
    }

    public PostDTO getPostInfo(Long postId) {
        SysPostEntity byId = postService.getById(postId);
        return new PostDTO(byId);
    }

    public void addPost(AddPostCommand addCommand) {
        PostModel postModel = postModelFactory.create();
        postModel.loadFromAddCommand(addCommand);

        postModel.checkPostNameUnique();
        postModel.checkPostCodeUnique();

        postModel.insert();
    }

    public void updatePost(UpdatePostCommand updateCommand) {
        PostModel postModel = postModelFactory.loadById(updateCommand.getPostId());
        postModel.loadFromUpdateCommand(updateCommand);

        postModel.checkPostNameUnique();
        postModel.checkPostCodeUnique();

        postModel.updateById();
    }


    public void deletePost(BulkOperationCommand<Long> deleteCommand) {
        for (Long id : deleteCommand.getIds()) {
            PostModel postModel = postModelFactory.loadById(id);
            postModel.checkCanBeDelete();
        }

        postService.removeBatchByIds(deleteCommand.getIds());
    }

}
