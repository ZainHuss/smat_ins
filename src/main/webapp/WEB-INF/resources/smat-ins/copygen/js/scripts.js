!(function (NioApp) {
  "use strict";

  /* Custom Menu (sidebar/header) */
  let nav = {
    classes: {
      main: 'nk-menu',
      item: 'nk-menu-item',
      link: 'nk-menu-link',
      toggle: 'nk-menu-toggle',
      dropdown: 'nk-menu-dropdown',
      dropdownparent: 'has-dropdown',
      active: 'active',
      current: 'current-page'
    },
  };

  NioApp.Dropdown = {
    load: function (elm, dropdownparent) {
      let parent = elm.parentElement;
      if (!parent.classList.contains(dropdownparent)) {
        parent.classList.add(dropdownparent);
      }
    },
    toggle: function (elm, active) {
      let parent = elm.parentElement;
      let nextelm = elm.nextElementSibling;
      let speed = nextelm.children.length > 5 ? 400 + nextelm.children.length * 10 : 400;
      if (!parent.classList.contains(active)) {
        parent.classList.add(active);
        NioApp.SlideDown(nextelm, speed);
      } else {
        parent.classList.remove(active);
        NioApp.SlideUp(nextelm, speed);
      }
    },
    closeSiblings: function (elm, active, dropdownparent, submenu) {
      let parent = elm.parentElement;
      let siblings = parent.parentElement.children;
      Array.from(siblings).forEach(item => {
        if (item !== parent) {
          item.classList.remove(active);
          if (item.classList.contains(dropdownparent)) {
            let subitem = item.querySelectorAll(`.${submenu}`);
            subitem.forEach(child => {
              child.parentElement.classList.remove(active);
              NioApp.SlideUp(child, 400);
            })
          }
        }
      });
    }
  }

  NioApp.Dropdown.header = function (selector) {
    const elm = document.querySelectorAll(selector);
    let active = nav.classes.active;
    let dropdownparent = nav.classes.dropdownparent;
    let dropdownmenu = nav.classes.dropdown;
    let menuCollapse = NioApp.body.dataset.menuCollapse ? NioApp.Break[NioApp.body.dataset.menuCollapse] : NioApp.Break.lg;
    elm.forEach(item => {
      NioApp.Dropdown.load(item, dropdownparent);
      item.addEventListener("click", function (e) {
        e.preventDefault();
        if (NioApp.Win.width < menuCollapse) {
          NioApp.Dropdown.toggle(item, active);
          NioApp.Dropdown.closeSiblings(item, active, dropdownparent, dropdownmenu);
        }
      });
    })
  }

  /* Custom Header Menu */
  let header = {
    classes: {
      root: 'nk-header-main',
      base: 'nk-header-menu',
      toggle: 'header-menu-toggle',
      toggleActive: 'active',
      active: 'header-menu-active',
      overlay: 'header-menu-overlay',
      body: 'header-menu-shown',
    },
    break: {
      main: NioApp.body.dataset.menuCollapse ? NioApp.Break[NioApp.body.dataset.menuCollapse] : NioApp.Break.lg,
    }
  };

  NioApp.Navbar = {
    show: function (toggle, target) {
      toggle.forEach(toggleItem => {
        toggleItem.classList.add(header.classes.toggleActive);
      })
      target.classList.add(header.classes.active);
      NioApp.body.classList.add(header.classes.body);
      let overalyTemplate = `<div class='${header.classes.overlay}'></div>`
      target.insertAdjacentHTML('beforebegin', overalyTemplate);
    },
    hide: function (toggle, target) {
      toggle.forEach(toggleItem => {
        toggleItem.classList.remove(header.classes.toggleActive);
      })
      target.classList.remove(header.classes.active);
      NioApp.body.classList.remove(header.classes.body);
      let overlay = document.querySelector(`.${header.classes.overlay}`);
      setTimeout(() => {
        overlay && overlay.remove();
      }, 400);
    },
    mobile: function (target) {
      if (header.break.main < NioApp.Win.width) {
        target.classList.remove('menu-mobile');
      } else {
        setTimeout(() => {
          target.classList.add('menu-mobile');
        }, 500);
      }
    },
    sticky: function (target) {
      let elem = document.querySelectorAll(target);
      if (elem.length > 0) {
        elem.forEach(item => {
          let _item_offset = item.offsetTop;
          
          window.addEventListener("scroll", function () {
            if (window.scrollY > _item_offset) {
              item.classList.add('has-fixed');
            } else {
              item.classList.remove('has-fixed');
            }
          });
        });
      }
    },
    height: function (target) {
      let elem = document.querySelectorAll(target);
      if (elem.length > 0) {
        elem.forEach(item => {
          document.querySelector('html').style.setProperty('--header-main-height', `${item.offsetHeight}px`)
        });
      }
      
    }
  }
  NioApp.Navbar.init = function () {
    let targetSl = document.querySelector(`.${header.classes.base}`);
    let toggleSl = document.querySelectorAll(`.${header.classes.toggle}`);
    toggleSl.forEach(item => {
      NioApp.Navbar.mobile(targetSl);
      item.addEventListener("click", function (e) {
        e.preventDefault();
        if (header.break.main > NioApp.Win.width) {
          if (!targetSl.classList.contains(header.classes.active)) {
            NioApp.Navbar.show(toggleSl, targetSl);
          } else {
            NioApp.Navbar.hide(toggleSl, targetSl);
          }
        }
      });

      window.addEventListener("resize", function (e) {
        if (header.break.main < NioApp.Win.width) {
          NioApp.Navbar.hide(toggleSl, targetSl);
        }
        NioApp.Navbar.mobile(targetSl);
      });

      document.addEventListener("mouseup", function (e) {
        if (e.target.closest(`.${header.classes.base}`) === null) {
          NioApp.Navbar.hide(toggleSl, targetSl);
        }
      });
    })
    NioApp.Navbar.sticky(`.nk-header .${header.classes.root}`);
    
    window.addEventListener("scroll", function () {
      NioApp.Navbar.height(`.nk-header .${header.classes.root}`)
    });
    
    window.addEventListener("resize", function () {
      NioApp.Navbar.height(`.nk-header .${header.classes.root}`)
    });
  }


  /* Add some class to current link */
  NioApp.CurrentLink = function (selector, parent, submenu, base, active, intoView) {
    let elm = document.querySelectorAll(selector);
    let currentURL = document.location.href,
      removeHash = currentURL.substring(0, (currentURL.indexOf("#") == -1) ? currentURL.length : currentURL.indexOf("#")),
      removeQuery = removeHash.substring(0, (removeHash.indexOf("?") == -1) ? removeHash.length : removeHash.indexOf("?")),
      fileName = removeQuery;

    elm.forEach(function (item) {
      var selfLink = item.getAttribute('href');
      if (fileName.match(selfLink)) {
        let parents = NioApp.getParents(item, `.${base}`, parent);
        parents.forEach(parentElemets => {
          parentElemets.classList.add(...active);
          let subItem = parentElemets.querySelector(`.${submenu}`);
          subItem !== null && (subItem.style.display = "block")
        })
        intoView && item.scrollIntoView({ block: "end" })
      } else {
        item.parentElement.classList.remove(...active);
      }
    })
  }


  /* Swiper slider */
  NioApp.Addons.swiperCarousel = function (selector) {
    let elem = document.querySelectorAll(selector);
    if (elem.length > 0) {
      elem.forEach(item => {
        let _breakpoints = item.dataset.breakpoints ? JSON.parse(item.dataset.breakpoints) : null;
        let _autoplay = item.dataset.autoplay ? JSON.parse(item.dataset.autoplay) : false;
        let _loop = item.dataset.loop ? JSON.parse(item.dataset.loop) : false;
        let _centeredSlides = item.dataset.centeredslides ? JSON.parse(item.dataset.centeredslides) : false;
        let _slidesPerView = item.dataset.slidesperview ? item.dataset.slidesperview : 1;
        let _speed = item.dataset.speed ? parseInt(item.dataset.speed) : 900;
        let _spaceBetween = item.dataset.spaceBetween ? parseInt(item.dataset.spaceBetween) : 0;
        let _effect = item.dataset.effect ? item.dataset.effect : 'slide';
        console.log(_breakpoints,_autoplay,_loop,_centeredSlides,_slidesPerView,_speed,_spaceBetween,_effect);
        const swiper = new Swiper(item, {
          centeredSlides: _centeredSlides,
          slidesPerView: _slidesPerView,
          loop: _loop,
          speed: _speed,
          autoplay: _autoplay,
          spaceBetween: _spaceBetween,
          effect: _effect,
          pagination: {
            el: ".swiper-pagination",
            type: 'bullets',
            clickable: true,
          },
          navigation: {
            prevEl: ".swiper-button-prev",
            nextEl: ".swiper-button-next",
            clickable: true,
          },
          breakpoints: _breakpoints,
        });

      });
    }
  }

  /* typed text rotator */
  NioApp.Addons.typed = function (selector) {
    let elem = document.querySelectorAll(selector);
    if (elem.length > 0) {
      elem.forEach(item => {
        console.log()
        let strings = JSON.parse("[" + item.dataset.strings + "]");
        new Typed(item, {
          strings: strings,
          typeSpeed: 100,
          backSpeed: 0,
          backDelay: 1000,
          startDelay: 0,
          loop: true,
        });
      });
    }
  }



  /* Pristine - Form Validation */
  NioApp.Addons.pristine = function (elem, live) {
    
    let config = {
      parent : "form-control-wrap",
      error : "form-error",
      success : "form-sucess",
      message : "form-error-message",
      messageTag : "span"
    }

    const pristine = new Pristine(elem, {
      classTo: config.parent,
      errorClass: config.error,
      successClass: config.success,
      errorTextParent: config.parent,
      errorTextTag: config.messageTag,
      errorTextClass: config.message
    }, live);

    return pristine;
  }

  /* Form Submission */
  NioApp.Custom.submitForm = function (selector) {
    let elm = document.querySelectorAll(selector);
    if (elm) {
      elm.forEach(item => {
        const formAction = item.dataset.action;

        let formValidate = NioApp.Addons.pristine(item, true)
        item.addEventListener('submit', function (e) {
          e.preventDefault();
          let valid = formValidate.validate();
          let result = item.querySelector('.form-result')

          if (valid) {
            let data = new FormData(item);
            const xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
              if (this.readyState == 4 && this.status == 200) {
                let res = null;
                try { res = JSON.parse(xhttp.responseText) } catch (e) { }
                result.classList.add('form-result-show');
                result.style.display = 'block';
                if (res) {
                  result.innerHTML = res.message;
                  if(res.result == 'success'){
                    result.classList.add('form-result-success');
                    result.classList.remove('form-result-error');
                    setTimeout(function(){
                      result.style.display = 'none';
                    },8000)
                  }else{
                    result.classList.add('form-result-error');
                    result.classList.remove('form-result-success');
                  }
                } else {
                  result.classList.remove('form-result-success');
                  result.classList.add('form-result-error');
                  result.innerHTML = "error", "Oops! There was something went wrong.";
                }
              }
            };

            xhttp.open("POST", formAction, true);
            xhttp.send(data);

            // Clear Input Field 
            item.reset();
          }

        });
      })
    }
  }

  NioApp.Custom.priceToggle = function (selector){
    let elm = document.querySelectorAll(selector);
    if (elm) {
      elm.forEach(item => {
        item.addEventListener('click', function(){
          let parent = item.closest(`.${item.dataset.parent}`);
          let target = document.querySelectorAll(`.${item.dataset.target}`);
          console.log(target);
          parent.classList.contains('pricing-yearly') ? parent.classList.remove('pricing-yearly'): parent.classList.add('pricing-yearly');
          target.forEach(item => {
            item.classList.contains('show-yearly') ? item.classList.remove('show-yearly'): item.classList.add('show-yearly');
          })
        })
      })
    }
  }

  NioApp.Custom.showHidePassword = function (selector) {
    let elem = document.querySelectorAll(selector);
    if (elem) {
      elem.forEach(item => {
        item.addEventListener("click", function (e) {
          e.preventDefault();
          let target = document.getElementById(item.getAttribute("href"));

          if (target.type == "password") {
            target.type = "text";
            item.classList.add("is-shown");
          } else {
            target.type = "password";
            item.classList.remove("is-shown");
          }
        });

      });
    }
  }

  NioApp.Custom.darkmode = function (selector){
    let elm = document.querySelectorAll(selector);
    if (elm) {
      elm.forEach(item => {
        item.addEventListener('click', function(){
          document.body.classList.contains('is-dark') ? document.body.classList.remove('is-dark'): document.body.classList.add('is-dark');
          elm.forEach(item => {
            item.classList.contains('dark-active') ? item.classList.remove('dark-active'): item.classList.add('dark-active');
          })
        })
      })
    }
  }

  /* Custom Scripts init */
  NioApp.Custom.init = function () {
    NioApp.Navbar.init();
    NioApp.Addons.swiperCarousel('.swiper-init');
    NioApp.Addons.typed('.type-init');
    NioApp.Custom.submitForm('.form-submit-init');
    NioApp.Custom.priceToggle('.pricing-toggle');
    NioApp.Custom.showHidePassword('.password-toggle');
    NioApp.Custom.darkmode('.dark-mode-toggle')
    NioApp.Dropdown.header(`.${nav.classes.toggle}`);
    NioApp.CurrentLink(`.${nav.classes.link}`, nav.classes.item, nav.classes.sub, nav.classes.main,[nav.classes.active, nav.classes.current],true);
  }

  // Initial by default
  /////////////////////////////
  NioApp.init = function () {
    NioApp.winLoad(NioApp.Custom.init);
  }
  NioApp.init();

  return NioApp;
})(NioApp);
