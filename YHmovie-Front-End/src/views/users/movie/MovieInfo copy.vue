<template>
  <div class="movie-info-container" v-if="movieDetail">
    <!-- Hero section with backdrop and poster -->
    <div class="movie-hero" :style="{ backgroundImage: `url(${movieDetail.moviePosterUrl})` }">
      <div class="hero-overlay"></div>
      <div class="container">
        <div class="hero-content">
          <div class="movie-poster">
            <img :src="movieDetail.moviePosterUrl" :alt="movieDetail.movieName" />
          </div>
          <div class="movie-header">
            <h1 class="movie-title">{{ movieDetail.movieName }}</h1>
            <div class="movie-meta">
              <span class="rating">
                <i class="iconfont icon-star"></i>
                {{ movieDetail.movieRating }}
              </span>
              <span class="duration">{{ movieDetail.movieDuration }}分钟</span>
              <span class="country">{{ movieDetail.movieCountry }}</span>
              <span class="language">{{ movieDetail.movieLanguage }}</span>
            </div>
            <div class="movie-tags">
              <span class="tag" v-for="(type, index) in uniqueMovieTypes" :key="index">
                {{ type }}
              </span>
            </div>
            <div class="movie-release">
              上映日期：{{ formatDate(movieDetail.movieReleaseDate) }}
            </div>
            <div class="box-office">
              票房：{{ formatBoxOffice(movieDetail.movieBoxOffice) }}
            </div>
            <div class="action-buttons">
              <button class="btn-buy-ticket" @click="goToCinemas">立即购票</button>
              <button class="btn-favorite">
                <i class="iconfont icon-heart"></i>
                收藏
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Movie details section -->
    <div class="main-content">
      <div class="container">
        <div class="content-wrapper">
          <!-- Left column for info sections -->
          <div class="left-column">
            <div class="movie-section">
              <h2 class="section-title">剧情简介</h2>
              <div class="movie-description">
                {{ movieDetail.movieDescription }}
              </div>
            </div>

            <div class="movie-section">
              <h2 class="section-title">演职人员</h2>
              <div class="cast-crew-list">
                <div class="cast-crew-category" v-if="directors.length > 0">
                  <h3>导演</h3>
                  <div class="actor-list">
                    <div class="actor-card" v-for="actor in directors" :key="actor.actorId">
                      <div class="actor-avatar">
                        <img :src="actor.actorAvatarUrl" :alt="actor.actorName" />
                      </div>
                      <div class="actor-name">{{ actor.actorName }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="cast-crew-category" v-if="actors.length > 0">
                  <h3>演员</h3>
                  <div class="actor-list">
                    <div class="actor-card" v-for="actor in actors" :key="actor.actorId">
                      <div class="actor-avatar">
                        <img :src="actor.actorAvatarUrl" :alt="actor.actorName" />
                      </div>
                      <div class="actor-name">{{ actor.actorName }}</div>
                      <div class="character-name">饰 {{ actor.characterName }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 评论区改进 -->
            <div class="movie-section comments-section">
              <div class="comments-header">
                <h2 class="section-title">观众评论</h2>
              </div>
              
              <!-- 评论发表表单 -->
              <div class="comment-form-container">
                <div class="comment-form">
                  <div class="comment-input-area">
                    <el-avatar :size="36" :src="userStore.userInfo?.userAvatar" class="user-avatar">
                      {{ userStore.userInfo?.userName?.charAt(0) || 'U' }}
                    </el-avatar>
                    <div class="input-wrapper">
                      <el-input
                        v-model="newComment"
                        type="textarea"
                        :rows="3"
                        placeholder="写下你的观影感受..."
                        maxlength="500"
                        show-word-limit
                        resize="none"
                        class="comment-textarea"
                      />
                      <div class="comment-actions">
                        <el-button
                          type="primary"
                          :disabled="!newComment.trim()"
                          :loading="submittingComment"
                          @click="submitComment"
                          size="small"
                        >
                          发表评论
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            
              <!-- 评论列表 -->
              <div class="comments-list" v-loading="loadingComments && comments.length === 0">
                <div 
                  v-for="comment in comments" 
                  :key="comment.commentId"
                  class="comment-item"
                >
                  <!-- 主评论 -->
                  <div class="comment-main">
                    <el-avatar 
                      :size="40" 
                      :src="comment.userAvatarUrl"
                      class="comment-avatar"
                    >
                      {{ comment.userName?.charAt(0) }}
                    </el-avatar>
                    
                    <div class="comment-content-area">
                      <div class="comment-header">
                        <div class="comment-header-left">
                          <span class="comment-username">{{ comment.userName }}</span>
                          <span class="comment-time">{{ formatDateTime(comment.createTime) }}</span>
                        </div>
                        
                        <!-- 点赞按钮移到最右侧 -->
                        <div class="comment-header-right">
                          <el-button
                            text
                            size="large"
                            :class="{ 'liked': comment.likeStatus }"
                            @click="handleLike(comment)"
                            class="comment-like-btn"
                          >
                            <el-icon :size="18">
                              <svg v-if="comment.likeStatus" viewBox="0 0 1024 1024" width="18" height="18">
                                <path d="M512 896c-12.8 0-25.6-4.8-35.2-14.4L166.4 563.2c-76.8-76.8-76.8-201.6 0-278.4 38.4-38.4 89.6-57.6 140.8-57.6s102.4 19.2 140.8 57.6L512 348.8l64-64c76.8-76.8 201.6-76.8 278.4 0 76.8 76.8 76.8 201.6 0 278.4L547.2 881.6c-9.6 9.6-22.4 14.4-35.2 14.4z" fill="#ff4757"/>
                              </svg>
                              <svg v-else viewBox="0 0 1024 1024" width="18" height="18">
                                <path d="M512 896c-12.8 0-25.6-4.8-35.2-14.4L166.4 563.2c-76.8-76.8-76.8-201.6 0-278.4 38.4-38.4 89.6-57.6 140.8-57.6s102.4 19.2 140.8 57.6L512 348.8l64-64c76.8-76.8 201.6-76.8 278.4 0 76.8 76.8 76.8 201.6 0 278.4L547.2 881.6c-9.6 9.6-22.4 14.4-35.2 14.4zM307.2 267.2c-38.4 0-76.8 14.4-105.6 43.2-57.6 57.6-57.6 153.6 0 211.2L512 832l310.4-310.4c57.6-57.6 57.6-153.6 0-211.2-57.6-57.6-153.6-57.6-211.2 0L512 409.6 413.6 310.4c-28.8-28.8-67.2-43.2-106.4-43.2z" fill="currentColor"/>
                              </svg>
                            </el-icon>
                            {{ comment.commentLikeNumber }}
                          </el-button>
                        </div>
                      </div>
                      
                      <div class="comment-text">{{ comment.commentContent }}</div>
                      
                      <div class="comment-footer">
                        <el-button
                          text
                          size="small"
                          @click="showReplyInput(comment)"
                          class="comment-reply-btn"
                        >
                          回复
                        </el-button>
            
                        <!-- 直接显示"展开回复"按钮，不需要点击回复就能看到 -->
                        <el-button
                          v-if="comment.replyCount > 0"
                          text
                          size="small"
                          @click="toggleReplies(comment)"
                          class="comment-expand-btn"
                        >
                          {{ comment.showReplies ? '收起回复' : `展开回复 (${comment.replyCount})` }}
                          <el-icon :class="{ 'rotated': comment.showReplies }">
                            <ArrowDown />
                          </el-icon>
                        </el-button>
                      </div>
            
                      <!-- 回复输入框 -->
                      <div v-if="replyingToCommentId === comment.commentId" class="reply-form">
                        <div class="reply-input-area">
                          <el-avatar :size="32" :src="userStore.userInfo?.userAvatar" class="user-avatar">
                            {{ userStore.userInfo?.userName?.charAt(0) || 'U' }}
                          </el-avatar>
                          <div class="input-wrapper">
                            <el-input
                              v-model="replyComment"
                              type="textarea"
                              :rows="2"
                              :placeholder="`回复 @${comment.userName}:`"
                              maxlength="300"
                              show-word-limit
                              resize="none"
                              class="reply-textarea"
                            />
                            <div class="reply-actions">
                              <el-button
                                size="small"
                                @click="cancelReply"
                              >
                                取消
                              </el-button>
                              <el-button
                                type="primary"
                                size="small"
                                :disabled="!replyComment.trim()"
                                :loading="submittingReply"
                                @click="submitReply(comment)"
                              >
                                回复
                              </el-button>
                            </div>
                          </div>
                        </div>
                      </div>
            
                      <!-- 回复列表 - 背景色保持和原背景相同 -->
                      <div 
                        v-if="comment.showReplies && comment.replies && comment.replies.length > 0"
                        class="replies-list"
                      >
                        <div 
                          v-for="reply in comment.replies"
                          :key="reply.commentId"
                          class="reply-item"
                        >
                          <el-avatar 
                            :size="32" 
                            :src="reply.userAvatarUrl"
                            class="reply-avatar"
                          >
                            {{ reply.userName?.charAt(0) }}
                          </el-avatar>
                          
                          <div class="reply-content-area">
                            <div class="reply-header">
                              <div class="reply-header-left">
                                <span class="reply-username">{{ reply.userName }}</span>
                                <span v-if="reply.repliedUserName" class="reply-target">
                                  回复 @{{ reply.repliedUserName }}
                                </span>
                                <span class="reply-time">{{ formatDateTime(reply.createTime) }}</span>
                              </div>
                              
                              <!-- 回复的点赞按钮也移到右侧 -->
                              <div class="reply-header-right">
                                <el-button
                                  text
                                  size="small"
                                  :class="{ 'liked': reply.likeStatus }"
                                  @click="handleLike(reply)"
                                  class="reply-like-btn"
                                >
                                  <el-icon :size="20">
                                    <svg v-if="reply.likeStatus" viewBox="0 0 1024 1024" width="16" height="16">
                                      <path d="M512 896c-12.8 0-25.6-4.8-35.2-14.4L166.4 563.2c-76.8-76.8-76.8-201.6 0-278.4 38.4-38.4 89.6-57.6 140.8-57.6s102.4 19.2 140.8 57.6L512 348.8l64-64c76.8-76.8 201.6-76.8 278.4 0 76.8 76.8 76.8 201.6 0 278.4L547.2 881.6c-9.6 9.6-22.4 14.4-35.2 14.4z" fill="#ff4757"/>
                                    </svg>
                                    <svg v-else viewBox="0 0 1024 1024" width="16" height="16">
                                      <path d="M512 896c-12.8 0-25.6-4.8-35.2-14.4L166.4 563.2c-76.8-76.8-76.8-201.6 0-278.4 38.4-38.4 89.6-57.6 140.8-57.6s102.4 19.2 140.8 57.6L512 348.8l64-64c76.8-76.8 201.6-76.8 278.4 0 76.8 76.8 76.8 201.6 0 278.4L547.2 881.6c-9.6 9.6-22.4 14.4-35.2 14.4zM307.2 267.2c-38.4 0-76.8 14.4-105.6 43.2-57.6 57.6-57.6 153.6 0 211.2L512 832l310.4-310.4c57.6-57.6 57.6-153.6 0-211.2-57.6-57.6-153.6-57.6-211.2 0L512 409.6 413.6 310.4c-28.8-28.8-67.2-43.2-106.4-43.2z" fill="currentColor"/>
                                    </svg>
                                  </el-icon>
                                  {{ reply.commentLikeNumber }}
                                </el-button>
                              </div>
                            </div>
                            
                            <div class="reply-text">{{ reply.commentContent }}</div>
                            
                            <div class="reply-footer">
                              <el-button
                                text
                                size="small"
                                @click="showReplyInputForReply(comment, reply)"
                                class="reply-reply-btn"
                              >
                                回复
                              </el-button>
                            </div>
                          </div>
                        </div>
                      </div>
            
                      <!-- 加载更多回复 -->
                      <div 
                        v-if="comment.showReplies && comment.hasMoreReplies"
                        class="load-more-replies"
                      >
                        <el-button
                          text
                          size="small"
                          :loading="comment.loadingReplies"
                          @click="loadMoreReplies(comment)"
                          class="load-more-btn"
                        >
                          加载更多回复
                        </el-button>
                      </div>
            
                      <!-- 加载回复中 -->
                      <div v-if="comment.showReplies && comment.loadingReplies" class="loading-replies">
                        <el-icon class="is-loading"><Loading /></el-icon>
                        <span>加载回复中...</span>
                      </div>
                    </div>
                  </div>
                </div>
            
                <!-- 加载更多评论 -->
                <div v-if="hasMoreComments && !loadingComments" class="load-more-comments">
                  <el-button text size="large" @click="loadMoreComments" class="load-more-btn">
                    加载更多评论
                  </el-button>
                </div>
            
                <!-- 加载评论中 -->
                <div v-if="loadingComments" class="loading-more-comments">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <span>加载评论中...</span>
                </div>
            
                <!-- 空状态 -->
                <div v-if="comments.length === 0 && !loadingComments" class="empty-comments">
                  <el-empty description="暂无评论，快来抢沙发吧！" :image-size="80" />
                </div>
              </div>
            </div>
          </div>
          
          <!-- Right column for related content -->
          <div class="right-column">
            <!-- 这里可以添加相关电影推荐或其他内容 -->
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading-container">
    <div class="loading-spinner"></div>
    <div>加载中...</div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { movieDetailById, movieActors } from '@/api/movies'
import { 
  commentsByMovieId, 
  commentsByCommentId, 
  likeComment, 
  addMovieComment, 
  addReplyComment 
} from '@/api/comments'
import { ElMessage } from 'element-plus'
import { ArrowDown, Loading } from '@element-plus/icons-vue'
export default {
  name: 'MovieInfo',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const movieDetail = ref(null)
    const actorsList = ref([])
    const loading = ref(true)
    
    // 评论相关状态
    const comments = ref([])
    const commentPage = ref(1)
    const commentPageSize = ref(10)
    const hasMoreComments = ref(true)
    const loadingComments = ref(false)
    const newComment = ref('')
    const replyComment = ref('')
    const replyingToCommentId = ref(null)
    const replyingToUserId = ref(null)
    const submittingComment = ref(false)
    const submittingReply = ref(false)
    
    // 用户信息（由于没有stores目录，使用本地模拟数据）
    const userStore = {
      userInfo: {
        userAvatar: '',
        userName: '用户'
      }
    }
    
    const fetchMovieDetail = async (movieId) => {
      try {
        // 移除不正确的解构
        const movieResponse = await movieDetailById(movieId)
        console.log('电影详情响应:', movieResponse)
        
        if (movieResponse.code === 200) {
          movieDetail.value = movieResponse.data
          console.log('设置电影详情成功:', movieDetail.value)
        } else {
          throw new Error(movieResponse.msg || '获取电影详情失败')
        }
      } catch (error) {
        console.error('获取电影详情失败:', error)
        ElMessage.error('获取电影详情失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    const fetchMovieActors = async (movieId) => {
      try {
        // 同样修正解构
        const actorsResponse = await movieActors(movieId)
        console.log('演员列表响应:', actorsResponse)
        
        if (actorsResponse.code === 200) {
          actorsList.value = actorsResponse.data.rows || actorsResponse.data
          console.log('设置演员列表成功:', actorsList.value)
        } else {
          throw new Error(actorsResponse.msg || '获取演员列表失败')
        }
      } catch (error) {
        console.error('获取演员列表失败:', error)
        ElMessage.error('获取演员列表失败: ' + error.message)
      }
    }
    
    // 获取电影评论
    const fetchMovieComments = async (reset = false) => {
      if (reset) {
        commentPage.value = 1
        comments.value = []
        hasMoreComments.value = true
      }
      
      if (!hasMoreComments.value || !movieDetail.value) return
      
      try {
        loadingComments.value = true
        const response = await commentsByMovieId(
          movieDetail.value.movieId, 
          commentPage.value, 
          commentPageSize.value
        )
        
        if (response.code === 200) {
          const newComments = response.data.rows || []
          
          // 为每条评论添加额外属性
          newComments.forEach(comment => {
            comment.showReplies = false
            comment.replies = []
            comment.replyCount = 0 // 将从后端获取或通过API调用获取
            comment.hasMoreReplies = false
            comment.replyPage = 1
            comment.loadingReplies = false
            
            // 检查评论是否有回复
            checkCommentReplies(comment)
          })
          
          comments.value = [...comments.value, ...newComments]
          
          // 检查是否还有更多评论
          const total = response.data.total || 0
          hasMoreComments.value = comments.value.length < total
          
          if (hasMoreComments.value) {
            commentPage.value++
          }
        } else {
          throw new Error(response.msg || '获取评论失败')
        }
      } catch (error) {
        console.error('获取评论失败:', error)
        ElMessage.error('获取评论失败: ' + error.message)
      } finally {
        loadingComments.value = false
      }
    }
    
    // 检查评论是否有回复（获取回复数量）
        
    const checkCommentReplies = async (comment) => {
      if (!comment) return Promise.resolve()
      
      try {
        // 只获取第一页的第一条回复，主要是为了获取总数
        const response = await commentsByCommentId(
          comment.commentId, 
          1, 
          1
        )
        
        if (response.code === 200) {
          // 更新回复总数
          comment.replyCount = response.data.total || 0
          comment.hasMoreReplies = comment.replyCount > 0
        }
        return Promise.resolve(comment)
      } catch (error) {
        console.error('获取评论回复数量失败:', error)
        return Promise.reject(error)
      }
    }
    
    // 加载更多评论
    const loadMoreComments = () => {
      fetchMovieComments()
      // fetchCommentReplies(comment, true)
    }
    
    // 提交新评论
    const submitComment = async () => {
      if (!newComment.value.trim() || !movieDetail.value) return
      
      try {
        submittingComment.value = true
        const commentDto = {
          commentContent: newComment.value.trim(),
          movieId: movieDetail.value.movieId
        }
        
        const response = await addMovieComment(commentDto)
        
        if (response.code === 200) {
          ElMessage.success('评论发布成功')
          newComment.value = ''
          // 刷新评论列表
          fetchMovieComments(true)
        } else {
          throw new Error(response.msg || '评论发布失败')
        }
      } catch (error) {
        console.error('评论发布失败:', error)
        ElMessage.error('评论发布失败: ' + error.message)
      } finally {
        submittingComment.value = false
      }
    }
    
    // 显示回复输入框
    const showReplyInput = (comment) => {
      replyingToCommentId.value = comment.commentId
      replyingToUserId.value = comment.userId
      replyComment.value = ''
    }
    
    // 显示对回复的回复输入框
    const showReplyInputForReply = (parentComment, reply) => {
      replyingToCommentId.value = parentComment.commentId
      replyingToUserId.value = reply.userId
      replyComment.value = ''
    }
    
    // 取消回复
    const cancelReply = () => {
      replyingToCommentId.value = null
      replyingToUserId.value = null
      replyComment.value = ''
    }
    
    // 提交回复
    const submitReply = async (comment) => {
      if (!replyComment.value.trim() || !movieDetail.value) return
      
      try {
        submittingReply.value = true
        const commentDto = {
          commentId: comment.commentId,
          repliedUserId: replyingToUserId.value,
          commentContent: replyComment.value.trim(),
          movieId: movieDetail.value.movieId
        }
        
        const response = await addReplyComment(commentDto)
        
        if (response.code === 200) {
          ElMessage.success('回复发布成功')
          cancelReply()
          
          // 更新回复数
          comment.replyCount = (comment.replyCount || 0) + 1
          
          // 自动加载该评论的回复
          if (!comment.showReplies) {
            toggleReplies(comment)
          } else {
            // 如果已经显示回复，则刷新回复列表
            fetchCommentReplies(comment, true)
          }
        } else {
          throw new Error(response.msg || '回复发布失败')
        }
      } catch (error) {
        console.error('回复发布失败:', error)
        ElMessage.error('回复发布失败: ' + error.message)
      } finally {
        submittingReply.value = false
      }
    }
    
    // 获取评论的回复
    const fetchCommentReplies = async (comment, reset = false) => {
      if (!comment) return
      
      if (reset) {
        comment.replyPage = 1
        comment.replies = []
        comment.hasMoreReplies = true
      }
      
      if (!comment.hasMoreReplies) return
      
      try {
        comment.loadingReplies = true
        const response = await commentsByCommentId(
          comment.commentId, 
          comment.replyPage, 
          commentPageSize.value
        )
        
        if (response.code === 200) {
          const newReplies = response.data.rows || []
          comment.replies = [...comment.replies, ...newReplies]
          
          // 更新回复总数
          comment.replyCount = response.data.total || comment.replies.length
          
          // 检查是否还有更多回复
          comment.hasMoreReplies = comment.replies.length < (response.data.total || 0)
          
          if (comment.hasMoreReplies) {
            comment.replyPage++
          }
        } else {
          throw new Error(response.msg || '获取回复失败')
        }
      } catch (error) {
        console.error('获取回复失败:', error)
        ElMessage.error('获取回复失败: ' + error.message)
      } finally {
        comment.loadingReplies = false
      }
    }
    
    // 加载更多回复
    const loadMoreReplies = (comment) => {
      fetchCommentReplies(comment)
    }
    
    // 切换显示/隐藏回复
    const toggleReplies = async (comment) => {
      if (!comment) return
      
      if (comment.showReplies) {
        // 已显示，则隐藏
        comment.showReplies = false
      } else {
        // 未显示，则显示并加载回复
        comment.showReplies = true
        if (comment.replies.length === 0) {
          await fetchCommentReplies(comment)
        }
      }
    }
    
    // 处理点赞/取消点赞
    const handleLike = async (comment) => {
      if (!comment) return
      
      try {
        const response = await likeComment(comment.commentId)
        
        if (response.code === 200) {
          // 更新点赞状态和数量
          comment.likeStatus = !comment.likeStatus
          comment.commentLikeNumber += comment.likeStatus ? 1 : -1
        } else {
          throw new Error(response.msg || '操作失败')
        }
      } catch (error) {
        console.error('点赞操作失败:', error)
        ElMessage.error('点赞操作失败: ' + error.message)
      }
    }
    
    onMounted(async () => {
      const movieId = route.params.movieId
      if (movieId) {
        try {
          loading.value = true
          console.log('正在获取电影ID为', movieId, '的详情')
          await fetchMovieDetail(movieId)
          await fetchMovieActors(movieId)
          // 加载电影评论
          await fetchMovieComments()
        } catch (error) {
          console.error('电影详情页加载失败:', error)
        } finally {
          loading.value = false
        }
      } else {
        console.error('未提供电影ID')
        ElMessage.error('缺少电影ID参数')
        router.push('/users/movies')
      }
    })
    
    const directors = computed(() => {
      return actorsList.value.filter(actor => actor.movieRoleType === '导演')
    })
    
    const actors = computed(() => {
      return actorsList.value.filter(actor => actor.movieRoleType === '演员')
    })
    
    const uniqueMovieTypes = computed(() => {
      if (!movieDetail.value || !movieDetail.value.movieTypes) return []
      return [...new Set(movieDetail.value.movieTypes)]
    })
    
    const formatDate = (dateString) => {
      const options = { year: 'numeric', month: 'long', day: 'numeric' }
      return new Date(dateString).toLocaleDateString('zh-CN', options)
    }
    
    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return ''
      
      const date = new Date(dateTimeString)
      const now = new Date()
      const diffMs = now - date
      
      // 一分钟内
      if (diffMs < 60000) {
        return '刚刚'
      }
      
      // 一小时内
      if (diffMs < 3600000) {
        return Math.floor(diffMs / 60000) + '分钟前'
      }
      
      // 一天内
      if (diffMs < 86400000) {
        return Math.floor(diffMs / 3600000) + '小时前'
      }
      
      // 一周内
      if (diffMs < 604800000) {
        return Math.floor(diffMs / 86400000) + '天前'
      }
      
      // 超过一周，显示具体日期
      const options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' }
      return date.toLocaleDateString('zh-CN', options)
    }
    
    const formatBoxOffice = (boxOffice) => {
      if (boxOffice >= 100000000) {
        return (boxOffice / 100000000).toFixed(2) + '亿'
      } else if (boxOffice >= 10000) {
        return (boxOffice / 10000).toFixed(2) + '万'
      }
      return boxOffice.toFixed(2)
    }
    
    const goToCinemas = () => {
      router.push(`/users/cinemas?movieId=${movieDetail.value.movieId}`)
    }
    
    return {
      movieDetail,
      actorsList,
      directors,
      actors,
      uniqueMovieTypes,
      loading,
      formatDate,
      formatBoxOffice,
      goToCinemas,
      
      // 评论相关
      comments,
      hasMoreComments,
      loadingComments,
      newComment,
      replyComment,
      replyingToCommentId,
      loadMoreComments,
      submitComment,
      showReplyInput,
      showReplyInputForReply,
      cancelReply,
      submitReply,
      toggleReplies,
      loadMoreReplies,
      handleLike,
      formatDateTime,
      
      // 用户信息
      userStore,
      submittingComment,
      submittingReply
    }
  }
}
</script>

<style scoped>
.movie-info-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  color: #333;
}

.movie-hero {
  position: relative;
  height: 400px; /* 稍微降低了英雄区域高度 */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.9));
}

.container {
  width: 85%; /* 增大宽度使用更多空间 */
  max-width: 1400px; /* 增大最大宽度 */
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.hero-content {
  display: flex;
  align-items: flex-start;
  color: #fff;
}

.movie-poster {
  flex: 0 0 210px; /* 稍微减小了海报尺寸 */
  margin-right: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
  border-radius: 8px;
  overflow: hidden;
}

.movie-poster img {
  width: 100%;
  display: block;
}

.movie-header {
  flex: 1;
  padding-top: 5px; /* 添加了一点顶部内边距 */
}

.movie-title {
  font-size: 32px;
  margin: 0 0 15px;
  font-weight: 600;
}

.movie-meta {
  margin-bottom: 15px;
  font-size: 16px;
}

.movie-meta span {
  margin-right: 20px;
}

.movie-meta .rating {
  color: #ffb400;
  font-weight: bold;
  font-size: 18px;
}

.movie-tags {
  margin-bottom: 15px;
}

.tag {
  display: inline-block;
  padding: 5px 12px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  margin-right: 10px;
  margin-bottom: 8px;
  font-size: 14px;
}

.movie-release, .box-office {
  margin-bottom: 10px;
  font-size: 16px;
}

.action-buttons {
  margin-top: 25px;
}

.btn-buy-ticket {
  background-color: #ff5c38;
  color: white;
  border: none;
  padding: 10px 25px; /* 稍微减小了按钮尺寸 */
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-buy-ticket:hover {
  background-color: #ff3e14;
  transform: translateY(-2px);
}

.btn-favorite {
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: white;
  padding: 10px 20px; /* 稍微减小了按钮尺寸 */
  border-radius: 25px;
  margin-left: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-favorite:hover {
  border-color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

/* 新增主内容区域样式 */
.main-content {
  background-color: #fff;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  position: relative;
  padding-top: 30px;
}

.content-wrapper {
  display: flex;
  gap: 30px;
}

.left-column {
  flex: 16; /* 修改左侧列的宽度比例 */
  padding: 0 0 40px;
  max-width: 90%; /* 限制左栏宽度为容器的75% */
}

.right-column {
  flex: 1; /* 调整右侧列的宽度比例 */
  padding-top: 0; /* 移除顶部内边距，与左侧对齐 */
}

.movie-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 20px;
  position: relative;
  padding-left: 15px;
  border-left: 4px solid #ff5c38;
}

.movie-description {
  font-size: 16px;
  line-height: 1.8;
  color: #444;
  padding-right: 20px; /* 添加右内边距，避免文本贴得太紧 */
}

.cast-crew-category {
  margin-bottom: 30px;
}

.cast-crew-category h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #666;
}

.actor-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.actor-card {
  width: 128px;
  text-align: center;
}

.actor-avatar {
  width: 128px;
  height: 170px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

.actor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.actor-name {
  font-weight: 600;
  margin-bottom: 5px;
}

.character-name {
  font-size: 14px;
  color: #777;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: #666;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #ff5c38;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

.loading-spinner.small {
  width: 20px;
  height: 20px;
  border-width: 3px;
  margin-bottom: 0;
  margin-right: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计改进 */
@media (max-width: 992px) {
  .content-wrapper {
    flex-direction: column;
  }
  
  .left-column {
    max-width: 100%;
  }
  
  .right-column {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .container {
    width: 90%;
  }
  
  .hero-content {
    flex-direction: column;
    align-items: center;
    padding-top: 30px;
  }
  
  .movie-poster {
    margin-right: 0;
    margin-bottom: 20px;
    width: 180px;
  }
  
  .movie-header {
    text-align: center;
  }
  
  .movie-hero {
    height: auto;
    min-height: 480px;
    padding-bottom: 30px;
  }
}

/* 新的评论样式 */
.comments-section {
  margin-top: 50px;
  border-top: 1px solid #eee;
  padding-top: 30px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.comment-form-container {
  margin-bottom: 30px;
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 20px;
}

.comment-form {
  width: 100%;
}

.comment-input-area {
  display: flex;
  gap: 16px;
}

.user-avatar {
  cursor: pointer;
  background-color: #ff5c38;
  color: white;
  font-weight: 500;
}

.input-wrapper {
  flex: 1;
}

.comment-textarea {
  width: 100%;
  margin-bottom: 12px;
}

.comment-textarea :deep(.el-textarea__inner) {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s;
}

.comment-textarea :deep(.el-textarea__inner:focus) {
  border-color: #ff5c38;
  box-shadow: 0 0 0 2px rgba(255, 92, 56, 0.2);
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.comments-list {
  min-height: 100px;
}

.comment-item {
  border-bottom: 1px solid #f0f0f0;
  padding: 24px 0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-main {
  display: flex;
  gap: 16px;
}

.comment-avatar {
  cursor: pointer;
  background-color: #f0f0f0;
}

.comment-content-area {
  flex: 1;
}

/* 评论头部布局修改为两边对齐 */
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-header-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.comment-username {
  font-weight: 600;
  color: #333;
  margin-right: 12px;
  cursor: pointer;
}

.comment-username:hover {
  color: #ff5c38;
}

.comment-time {
  font-size: 13px;
  color: #999;
}

/* 右侧点赞按钮样式 */
.comment-header-right {
  display: flex;
  align-items: center;
}

.comment-like-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.comment-like-btn:hover {
  background-color: rgba(255, 71, 87, 0.08);
}

.comment-like-btn.liked {
  color: #ff4757;
}

.comment-text {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 12px;
  word-break: break-word;
  white-space: pre-wrap;
}

.comment-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.comment-footer .el-button {
  color: #666;
}

.comment-expand-btn {
  display: flex;
  align-items: center;
}

.comment-expand-btn .el-icon {
  margin-left: 4px;
  transition: transform 0.3s;
}

.comment-expand-btn .rotated {
  transform: rotate(180deg);
}

/* 回复表单 */
.reply-form {
  background-color: #fff;  /* 改为与原背景一致 */
  border: none;
  border-radius: 0;
  padding: 16px 0;
  margin-top: 12px;
  margin-bottom: 16px;
}

.reply-input-area {
  display: flex;
  gap: 12px;
}

.reply-textarea {
  width: 100%;
  margin-bottom: 12px;
}

.reply-textarea :deep(.el-textarea__inner) {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 回复列表 - 改为与原背景相同的颜色 */
.replies-list {
  background-color: #fff;
  border: none;
  border-radius: 0;
  margin-top: 16px;
  padding: 16px 0;
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar {
  cursor: pointer;
  background-color: #f0f0f0;
}

.reply-content-area {
  flex: 1;
}

/* 回复头部也调整为两边对齐 */
.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-header-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.reply-header-right {
  display: flex;
  align-items: center;
}

.reply-username {
  font-weight: 600;
  color: #333;
  margin-right: 6px;
  cursor: pointer;
}

.reply-username:hover {
  color: #ff5c38;
}

.reply-target {
  color: #666;
  margin-right: 8px;
}

.reply-time {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.reply-text {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
  word-break: break-word;
}

.reply-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 回复的点赞按钮 */
.reply-like-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 16px;
}

.reply-like-btn:hover {
  background-color: rgba(255, 71, 87, 0.08);
}

.reply-like-btn.liked {
  color: #ff4757;
}

.reply-reply-btn {
  font-size: 13px;
}

.load-more-replies {
  padding-top: 12px;
  text-align: center;
}

.loading-replies {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 0;
  color: #999;
  font-size: 13px;
}

.load-more-comments {
  text-align: center;
  padding: 20px 0;
}

.loading-more-comments {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 20px 0;
  color: #999;
}

.empty-comments {
  padding: 40px 0;
  text-align: center;
}

.load-more-btn {
  color: #ff5c38;
}

.load-more-btn:hover {
  background-color: rgba(255, 92, 56, 0.05);
}

/* 响应式调整 */
@media (max-width: 576px) {
  .comment-input-area, .reply-input-area {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .user-avatar {
    margin-bottom: 10px;
  }
  
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .comment-header-right {
    margin-top: 8px;
    align-self: flex-end;
  }
  
  .reply-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .reply-header-right {
    margin-top: 8px;
    align-self: flex-end;
  }
}
</style>