(function( $ ){
    // 当domReady的时候开始初始化
    $(function() {
        var $saas_uploader = $('input[saas-uploader]');

        if($saas_uploader.length <= 0) return;
        
        $saas_uploader.each(function(index, el) {
            var $self = $(this).attr({
                            type: 'hidden'
                        }),
                $_data = $self.data(),
                $_wrap = $('<div class="saas-uploader-wrap cf"/>'),
                $_queuelist = $('<ul class="saas-uploader-queuelist cf"/>'),
                $_btn = $('<div class="saas-uploader-btn"/>')
                $_options = {
                    accept: {
                        title: 'Images',
                        extensions: 'gif,jpg,jpeg,bmp,png',
                        mimeTypes: 'image/*'
                    },
                    fileNumLimit: $_data.numLimit || 2,
                    // fileSizeLimit: 200 * 1024 * 1024,    // 200 M
                    fileSingleSizeLimit: $_data.sizelimit / 100 || 0.5    // 0.5 M
                };

            $self.after( $_wrap.append($_queuelist, $_btn) );


            var $wrap = $_wrap,

                // 图片容器
                $queue = $_queuelist,

                // 优化retina, 在retina下这个值是2
                ratio = window.devicePixelRatio || 1,

                // 缩略图大小
                thumbnailWidth = 110 * ratio,
                thumbnailHeight = 110 * ratio,

                // 可能有pedding, ready, uploading, confirm, done.
                state = 'pedding',

                // 判断浏览器是否支持图片的base64
                isSupportBase64 = ( function() {
                    var data = new Image();
                    var support = true;
                    data.onload = data.onerror = function() {
                        if( this.width != 1 || this.height != 1 ) {
                            support = false;
                        }
                    }
                    data.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
                    return support;
                } )(),

                // 检测是否已经安装flash，检测flash的版本
                flashVersion = ( function() {
                    var version;

                    try {
                        version = navigator.plugins[ 'Shockwave Flash' ];
                        version = version.description;
                    } catch ( ex ) {
                        try {
                            version = new ActiveXObject('ShockwaveFlash.ShockwaveFlash')
                                    .GetVariable('$version');
                        } catch ( ex2 ) {
                            version = '0.0';
                        }
                    }
                    version = version.match( /\d+/g );
                    return parseFloat( version[ 0 ] + '.' + version[ 1 ], 10 );
                } )(),

                // WebUploader实例
                uploader;

            if ( !WebUploader.Uploader.support('flash') && WebUploader.browser.ie ) {

                // flash 安装了但是版本过低。
                if (flashVersion) {
                    (function(container) {
                        window['expressinstallcallback'] = function( state ) {
                            switch(state) {
                                case 'Download.Cancelled':
                                    alert('您取消了更新！')
                                    break;

                                case 'Download.Failed':
                                    alert('安装失败')
                                    break;

                                default:
                                    alert('安装已成功，请刷新！');
                                    break;
                            }
                            delete window['expressinstallcallback'];
                        };

                        var swf = './expressInstall.swf';
                        // insert flash object
                        var html = '<object type="application/' +
                                'x-shockwave-flash" data="' +  swf + '" ';

                        if (WebUploader.browser.ie) {
                            html += 'classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" ';
                        }

                        html += 'width="100%" height="100%" style="outline:0">'  +
                            '<param name="movie" value="' + swf + '" />' +
                            '<param name="wmode" value="transparent" />' +
                            '<param name="allowscriptaccess" value="always" />' +
                        '</object>';

                        container.html(html);

                    })($wrap);

                // 压根就没有安转。
                } else {
                    $wrap.html('<a href="http://www.adobe.com/go/getflashplayer" target="_blank" border="0"><img alt="get flash player" src="http://www.adobe.com/macromedia/style_guide/images/160x41_Get_Flash_Player.jpg" /></a>');
                }

                return;
            } else if (!WebUploader.Uploader.support()) {
                alert( 'Web Uploader 不支持您的浏览器！');
                return;
            }

            // 实例化
            uploader = WebUploader.create({
                auto: true,
                pick: {
                    id: $_btn,
                    innerHTML: '<i class="glyphicon glyphicon-picture"></i><span>点击选择图片</span>'
                },
                fileVal: 'files',
                formData: {
                    type: 'policy'
                },
                paste: '#saas-uploader',
                swf: './Uploader.swf',
                chunked: true,
                chunkSize: 512 * 1024,
                server: '/sys/file/upload',
                // runtimeOrder: 'flash',

                accept: $_options.accept,

                // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
                disableGlobalDnd: true,
                fileNumLimit: $_options.fileNumLimit,
                // fileSizeLimit: 200 * 1024 * 1024,    // 200 M
                fileSingleSizeLimit: $_options.fileSingleSizeLimit  * 1024 * 1024
            });

            uploader.onReady = function() {
                var queues = typeof $self.val() === 'string' ? $self.val().split(',') : $self.val();
                addQueue(queues);
            };

            uploader.onFileQueued = function( file ) {
                // addFile( file );
            };

            uploader.onFileDequeued = function( file ) {
                removeFile( file );
            };

            uploader.onUploadProgress = function( file, percentage ) {
                // var $li = $('#'+file.id),
                //     $percent = $li.find('.progress span');

                // $percent.css( 'width', percentage * 100 + '%' );
            };

            uploader.onUploadSuccess = function (file, response) {
                addQueue(response.data)
                uploader.reset();
            };

            uploader.onUploadError = function (file, reason) {
                console.log(reason);
            };

            uploader.onError = function( code ) {
                var msg;
                switch (code) {
                    case 'Q_EXCEED_NUM_LIMIT':
                        msg = '最多上传' + $_options.fileNumLimit + '张图片';
                        break;
                    case 'Q_TYPE_DENIED':
                        msg = '只能上传' + $_options.accept.extensions + '类型文件'
                        break;
                }
                $.confirm({
                    title: '错误提示',
                    content: msg,
                    closeIcon: true,
                    type: 'red',
                    typeAnimated: true,
                    buttons: {
                        yes: {
                            text: '确定'
                        }
                    }
                });
            };

            // 当有文件添加进来时执行，负责view的创建
            function addFile (file) {
                var $li = $( '<li id="' + file.id + '">' +
                        '<p class="imgWrap"></p>'+
                        '<p class="progress"><span></span></p>' +
                        '</li>' ),

                    $btns = $('<div class="file-panel"><span class="cancel">删除</span>').appendTo( $li ),
                    $prgress = $li.find('p.progress span'),
                    $wrap = $li.find( 'p.imgWrap' ),
                    $info = $('<p class="error"></p>'),

                    showError = function( code ) {
                        switch( code ) {
                            case 'exceed_size':
                                text = '文件大小超出';
                                break;

                            case 'interrupt':
                                text = '上传暂停';
                                break;

                            default:
                                text = '上传失败，请重试';
                                break;
                        }

                        $info.text( text ).appendTo( $li );
                    };

                if ( file.getStatus() === 'invalid' ) {
                    showError( file.statusText );
                } else {
                    // @todo lazyload
                    $wrap.text( '预览中' );
                    uploader.makeThumb( file, function( error, src ) {
                        var img;

                        if ( error ) {
                            $wrap.text( '不能预览' );
                            return;
                        }

                        if( isSupportBase64 ) {
                            img = $('<img src="'+src+'">');
                            $wrap.empty().append( img );
                        } else {
                            $wrap.text("预览出错");
                        }
                    }, thumbnailWidth, thumbnailHeight );
                    file.rotation = 0;
                }

                file.on('statuschange', function( cur, prev ) {
                    console.log(cur);
                    // debugger;
                    if ( prev === 'progress' ) {
                        $prgress.hide().width(0);
                    } else if ( prev === 'queued' ) {
                        $li.off( 'mouseenter mouseleave' );
                        $btns.remove();
                    }

                    // 成功
                    if ( cur === 'error' || cur === 'invalid' ) {
                        showError( file.statusText );
                    } else if ( cur === 'interrupt' ) {
                        showError( 'interrupt' );
                    } else if ( cur === 'queued' ) {
                        $info.remove();
                        $prgress.css('display', 'block');
                    } else if ( cur === 'progress' ) {
                        $info.remove();
                        $prgress.css('display', 'block');
                    } else if ( cur === 'complete' ) {
                        $prgress.hide().width(0);
                        $li.append( '<span class="success"></span>' );
                    }

                    $li.removeClass( 'state-' + prev ).addClass( 'state-' + cur );
                });

                $li.on( 'mouseenter', function() {
                    $btns.show();
                });

                $li.on( 'mouseleave', function() {
                    $btns.hide();
                });

                $btns.on( 'click', 'span', function() {
                    uploader.removeFile( file );
                });

                $li.appendTo( $queue );
            }

            // 负责view的销毁
            function removeFile (file) {
                var $li = $('#'+file.id);

                $li.off().find('.file-panel').off().end().remove();
            }

            function addQueue (queues) {
                if (Object.prototype.toString.call(queues) !== '[object Array]') return;
                $queue.empty();
                queues.forEach(function (value, index) {
                    var $li = $( '<li>' +
                        '<p class="imgWrap"><img src="' + value + '" style="margin-top:-3px;"></p>'+
                        '</li>' );
                    $li.appendTo( $queue );
                });
                $saas.errorImage();
                $self.setVal(queues);
            }
            // $upload.on('click', function() {
            //     if ( $(this).hasClass( 'disabled' ) ) {
            //         return false;
            //     }

            //     if ( state === 'ready' ) {
            //         uploader.upload();
            //     } else if ( state === 'paused' ) {
            //         uploader.upload();
            //     } else if ( state === 'uploading' ) {
            //         uploader.stop();
            //     }
            // });

            // $info.on( 'click', '.retry', function() {
            //     uploader.retry();
            // } );

            // $info.on( 'click', '.ignore', function() {
            //     alert( 'todo' );
            // } );

            // $upload.addClass( 'state-' + state );
            // updateTotalProgress();
        });
    });

})( jQuery );
