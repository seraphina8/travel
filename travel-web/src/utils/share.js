const ShareUtils = {
  shareToQQ(title, url, summary, pic) {
    const shareUrl = `https://connect.qq.com/widget/shareqq/index.html?` +
      `url=${encodeURIComponent(url)}` +
      `&title=${encodeURIComponent(title)}` +
      `&summary=${encodeURIComponent(summary || '')}` +
      `&pics=${encodeURIComponent(pic || '')}`
    window.open(shareUrl, '_blank', 'width=600,height=500')
  },

  shareToWeibo(title, url, pic) {
    const shareUrl = `https://service.weibo.com/share/share.php?` +
      `url=${encodeURIComponent(url)}` +
      `&title=${encodeURIComponent(title)}` +
      `&pic=${encodeURIComponent(pic || '')}`
    window.open(shareUrl, '_blank', 'width=600,height=500')
  },

  shareToWechat(url) {
    alert('请复制链接后在微信中分享：\n' + url)
  },

  copyLink(url) {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(url).then(() => {
        alert('链接已复制到剪贴板')
      })
    } else {
      const input = document.createElement('input')
      input.value = url
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      alert('链接已复制到剪贴板')
    }
  },

  nativeShare(title, text, url) {
    if (navigator.share) {
      navigator.share({ title, text, url })
        .catch(err => console.log('分享失败', err))
    } else {
      this.copyLink(url)
    }
  }
}

export default ShareUtils
