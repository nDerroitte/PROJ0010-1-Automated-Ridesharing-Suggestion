function [ s1,s2 ] = evalautocorr( period,reliability,variance,noise,min_point,offset )

s1 = zeros(1,length(period));
s2 = zeros(1,length(period));
for i=1 : length(period)
    for j=1 : 100
        [finded_period,~] = scenario(period,reliability,variance,noise,min_point,offset);
        if finded_period(1) == 10080
            s1(i) = s1(i) + 1;
        end
        if ismember(period(i),finded_period)
            s2(i) = s2(i) + 1;
        end
    end
end


end

