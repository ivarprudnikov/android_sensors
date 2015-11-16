'use strict';

/* jshint es3:false */

module.exports = function (grunt) {

  // Load grunt tasks automatically
  require('load-grunt-tasks')(grunt);

  // Time how long tasks take. Can help when optimizing build times
  require('time-grunt')(grunt);

  // Configurable paths for the application
  var appConfig = {
    app: 'sources',
    dist: 'dist'
  };

  // Define the configuration for all the tasks
  grunt.initConfig({

    // Project settings
    yeoman: appConfig,

    // Watches files for changes and runs tasks based on the changed files
    watch: {
      bower: {
        files: ['bower.json'],
        tasks: ['wiredep']
      },
      js: {
        files: [
	        '<%= yeoman.app %>/{,**/}*.js',
          '!<%= yeoman.app %>/{,**/}generated*.js'
        ],
        tasks: ['jshint'],
        options: {
          livereload: '<%= connect.options.livereload %>'
        }
      },
      compass: {
        files: ['<%= yeoman.app %>/styles/{,*/}*.{scss,sass}'],
        tasks: ['compass:server', 'autoprefixer']
      },
      gruntfile: {
        files: ['Gruntfile.js']
      },
      livereload: {
        options: {
          livereload: '<%= connect.options.livereload %>'
        },
        files: [
          '<%= yeoman.app %>/scripts/{,**/}*.js',
          '<%= yeoman.app %>/*.html',
          '.tmp/styles/{,*/}*.css',
          '<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
        ]
      }
    },

    // The actual grunt server settings
    connect: {
      options: {
        port: 9000,
        // Change this to '0.0.0.0' to access the server from outside.
        hostname: 'localhost',
        livereload: 35889
      },
      livereload: {
        options: {
          open: true,
          middleware: function (connect) {
            return [
              connect.static('.tmp'),
              connect().use(
                '/bower_components',
                connect.static('./bower_components')
              ),
              connect.static(appConfig.app)
            ];
          }
        }
      },
      dist: {
        options: {
          open: true,
          base: '<%= yeoman.dist %>'
        }
      }
    },

    // Make sure code styles are up to par and there are no obvious mistakes
    jshint: {
      options: {
        jshintrc: '.jshintrc'
      },
      main: {
        src: [
          'Gruntfile.js',
          '<%= yeoman.app %>/{,**/}*.js', // aggressive approach
	        '!<%= yeoman.app %>/{,**/}generated*.js'
        ]
      }
    },

    // Empties folders to start fresh
    clean: {
      dist: {
        files: [{
          dot: true,
          src: [
            '.tmp',
            '<%= yeoman.dist %>/{,*/}*',
            '!<%= yeoman.dist %>/.git{,*/}*'
          ]
        }]
      },
      server: '.tmp'
    },

    // Add vendor prefixed styles
    autoprefixer: {
      options: {
        browsers: ['last 5 version']
      },
      dist: {
        files: [{
          expand: true,
          cwd: '.tmp/styles/',
          src: '{,*/}*.css',
          dest: '.tmp/styles/'
        }]
      }
    },

    // Automatically inject Bower components into the app
    wiredep: {
      app: {
        src: ['<%= yeoman.app %>/index.html'],
        ignorePath:  /\.\.\//
      },
      sass: {
        src: ['<%= yeoman.app %>/styles/{,*/}*.{scss,sass}'],
        ignorePath: /(\.\.\/){1,2}bower_components\//
      }
    },

    // Compiles Sass to CSS and generates necessary files if requested
    compass: {
      options: {
        sassDir: '<%= yeoman.app %>/styles',
        cssDir: '.tmp/styles',
        generatedImagesDir: '.tmp/images/generated',
        imagesDir: '<%= yeoman.app %>/images',
        javascriptsDir: '<%= yeoman.app %>/scripts',
        fontsDir: '<%= yeoman.app %>/styles/fonts',
        importPath: './bower_components',
        httpImagesPath: '/images',
        httpGeneratedImagesPath: '/images/generated',
        httpFontsPath: '/styles/fonts',
        relativeAssets: false,
        assetCacheBuster: false,
        raw: 'Sass::Script::Number.precision = 10\n'
      },
      dist: {
        options: {
          generatedImagesDir: '<%= yeoman.dist %>/images/generated'
        }
      },
      server: {
        options: {
          sourcemap: true
        }
      }
    },

    // Renames files for browser caching purposes
    filerev: {
      dist: {
        src: [
          '<%= yeoman.dist %>/scripts/{,**/}*.js',
          '<%= yeoman.dist %>/styles/{,**/}*.css',
          '<%= yeoman.dist %>/images/{,**/}*.{png,jpg,jpeg,gif,webp,svg}',
          '<%= yeoman.dist %>/styles/fonts/*'
        ]
      }
    },

    // environment specific configuration
    ///////////////////////////////////////
    replace: {
      analyticsId: {
        options: {
          patterns: [{ json: grunt.file.readJSON('package.json') }]
        },
        files: [{
          src: '<%= yeoman.dist %>/index.html',
          dest: '<%= yeoman.dist %>/index.html'
        }]
      }
    },

    // Reads HTML for usemin blocks to enable smart builds that automatically
    // concat, minify and revision files. Creates configurations in memory so
    // additional tasks can operate on them
    useminPrepare: {
      html: '<%= yeoman.app %>/index.html',
      options: {
        dest: '<%= yeoman.dist %>',
        flow: {
          html: {
            steps: {
              js: ['concat'],
              css: ['concat']
            },
            post: {}
          }
        }
      }
    },

    // Performs rewrites based on filerev and the useminPrepare configuration
    usemin: {
      html: ['<%= yeoman.dist %>/{,*/}*.html'],
      css: ['<%= yeoman.dist %>/styles/{,*/}*.css'],
      options: {
        assetsDirs: [
          '<%= yeoman.dist %>',
          '<%= yeoman.dist %>/images',
          '<%= yeoman.dist %>/styles'
        ]
      }
    },

    // compress css
    ///////////////////////
    cssmin: {
      options: {
        banner: '',
        report: 'min'
      },
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= yeoman.dist %>',
            src: '**/*.css',
            dest: '<%= yeoman.dist %>'
          }
        ]
      }
    },

    htmlmin: {
      dist: {
        options: {
          // collapseWhitespace: true,
          // conservativeCollapse: true,
          // collapseBooleanAttributes: true,
          // removeCommentsFromCDATA: true,
          // removeOptionalTags: true
        },
        files: [{
          expand: true,
          cwd: '<%= yeoman.app %>',
          src: ['*.html'],
          dest: '<%= yeoman.dist %>'
        }]
      }
    },

    // uglify js files
    ///////////////////////////////////////
    uglify: {
      options: {
        mangle: false,
        compress: false,
        beautify: false
      },
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= yeoman.dist %>',
            src: '{,**/}*.js',
            dest: '<%= yeoman.dist %>'
          }
        ]
      }
    },

    // Copies remaining files to places other tasks can use
    copy: {
      dist: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= yeoman.app %>',
          dest: '<%= yeoman.dist %>',
          src: [
            '*.{ico,png,txt}',
            'styles/fonts/{,*/}*.*'
          ]
        }, {
          expand: true,
          cwd: '<%= yeoman.app %>/images',
          dest: '<%= yeoman.dist %>/images',
          src: '*'
        }, {
          expand: true,
          cwd: '.tmp/css',
          dest: '<%= yeoman.dist %>/css',
          src: '{,*/}*.css'
        }, {
          expand: true,
          cwd: '.',
          src: 'bower_components/bootstrap-sass/assets/fonts/bootstrap/*',
          dest: '<%= yeoman.dist %>'
        }]
      }
    },

    // Run some tasks in parallel to speed up the build process
    concurrent: {
      server: {
        tasks: ['compass:server'],
        options: {
          logConcurrentOutput: true
        }
      },
      dist: {
        tasks: [
          'compass:dist'   // compile sass
        ],
        options: {
          logConcurrentOutput: true
        }
      }
    }

  });

  grunt.registerTask('serve', 'Compile then start a connect web server', function (target) {

    if (target === 'dist') {
      return grunt.task.run([
        'build',
        'concurrent:server',
        'connect:dist:keepalive']);
    }

    grunt.task.run([
      'clean:server',
      'wiredep',
      'concurrent:server',
      'autoprefixer',   // prefix styles in tmp
      'connect:livereload',
      'watch'
    ]);
  });

  grunt.registerTask('build', 'build assets', function (target) {

    grunt.task.run([
      'jshint:main',
      'clean:dist',     // clean temp and dist directories
      'wiredep',        // make sure all installed bower dependencies are referenced in files
      'concurrent:dist',// compile sass, move images to dist
      'autoprefixer',   // prefix styles in tmp and app
      'useminPrepare',  // read html build blocks and prepare to concatenate and move css,js to dist
      'concat',         // concatinates files and moves them to dist
      'cssmin',         // minify css in dist
      'htmlmin',        // minify and copy html files to dist
      'copy:dist',      // move unhanled files to dist
      'uglify',         // minifies & uglifies js files in dist
      'filerev',        // hashes js/css/img/font files in dist
      'usemin',
      'replace:analyticsId'
    ]);

  });

  grunt.registerTask('default', [
    'build'
  ]);
};
