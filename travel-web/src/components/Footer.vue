<template>
  <footer class="footer">
    <div class="container">
      <div class="row">
        <div class="col-lg-4 col-md-6 mb-4">
          <h5><i class="bi bi-globe-asia-australia"></i> 智能旅游规划系统</h5>
          <p class="text-secondary mt-3">让每一次出行都成为难忘的回忆。</p>
          <div class="social-links mt-3">
            <a href="#" class="me-3"><i class="bi bi-wechat fs-5"></i></a>
            <a href="#" class="me-3"><i class="bi bi-tencent-qq fs-5"></i></a>
            <a href="#" class="me-3"><i class="bi bi-sina-weibo fs-5"></i></a>
          </div>
        </div>

        <div class="col-lg-2 col-md-6 mb-4">
          <h5>快速链接</h5>
          <ul class="footer-links">
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/scenic">热门景区</router-link></li>
            <li><router-link to="/strategy">旅游攻略</router-link></li>
          </ul>
        </div>

        <div class="col-lg-2 col-md-6 mb-4">
          <h5>用户服务</h5>
          <ul class="footer-links">
            <li><router-link to="/user">用户中心</router-link></li>
            <li><router-link to="/order">我的订单</router-link></li>
            <li><router-link to="/feedback">意见反馈</router-link></li>
          </ul>
        </div>

        <div class="col-lg-4 col-md-6 mb-4">
          <h5>联系我们</h5>
          <ul class="footer-links">
            <li><i class="bi bi-telephone me-2"></i>400-888-8888</li>
            <li><i class="bi bi-envelope me-2"></i>service@travel.com</li>
            <li><i class="bi bi-geo-alt me-2"></i>四川省成都市xxx</li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <p>
          &copy; 2024 畅游天下. All rights reserved. |
          <a href="#" class="text-secondary">隐私政策</a> |
          <a href="#" class="text-secondary">用户协议</a>
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.social-links a {
  color: #94a3b8;
  transition: color 0.3s;
}

.social-links a:hover {
  color: #fff;
}
</style>
