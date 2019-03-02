function y = extend(s)

y=zeros(1,sum(s));
todo = find(s>0);
j = 1;
while ~isempty(todo)
    for i=1 : length(todo)
        y(j) = todo(i);
        j = j+1;
    end
    s = s - 1 ;
    todo = find(s>0);
end
y = sort(y);
end
    
    
    



