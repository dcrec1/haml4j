require "rubygems"
require "haml"

CACHE = {}

def cache()
  return CACHE
end

def haml_engine_for(file_name) 
  cache[file_name] = Haml::Engine.new(File.read(file_name)) if cache[file_name].nil?
  return cache[file_name]
end

def render()
  return haml_engine_for($file).render
end